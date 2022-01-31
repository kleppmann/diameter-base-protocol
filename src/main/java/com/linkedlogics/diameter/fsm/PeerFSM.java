package com.linkedlogics.diameter.fsm;


import com.linkedlogics.application.exception.ExceptionUtility;
import com.linkedlogics.application.logger.Logger;
import com.linkedlogics.application.logger.LoggerLevel;
import com.linkedlogics.diameter.exception.InternalException;
import com.linkedlogics.diameter.exception.ParseException;
import com.linkedlogics.diameter.object.*;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PeerFSM {

    protected FSMState state = FSMState.CLOSED;
    protected State[] states;
    protected boolean watchdogSent;
    protected Context context;
    protected long timer;
    protected long CEA_TIMEOUT = 1000, IAC_TIMEOUT = 5000, DWA_TIMEOUT = 100, DPA_TIMEOUT = 100;
    protected Thread executor;
    protected BlockingQueue<StateEvent> eventQueue;
    protected final StateEvent TIMEOUT_EVENT = new FSMEvent(EventType.TIMEOUT_EVENT);
    private Lock lock = new ReentrantLock();
    protected Random random = new Random();

    public PeerFSM(Context context) {
        this.context = context;
        eventQueue = new LinkedBlockingDeque<>(100);
        startProcess();
    }

    private void startProcess() {
        executor = new Thread() {
            @Override
            public void run() {
                while (executor != null) {
                    StateEvent event;
                    try {
                        event = eventQueue.poll(100, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        ExceptionUtility.handleException(e);
                        executor = null;
                        break;
                    }
                    lock.lock();
                    try {
                        if (event != null) {
                            Logger.log(LoggerLevel.DEBUG, "Process event is %s, peer state is %s", event, state);
                            getStates()[state.getCode()].defaultAction(event);
                        }
                        if (timer != 0 && timer < System.currentTimeMillis()) {
                            timer = 0;
                            if (state != FSMState.CLOSED) {
                                handleEvent(TIMEOUT_EVENT);
                            }
                        }
                    } catch (Exception e) {
                        Logger.log(LoggerLevel.DEBUG, e, "Exception during processing FSM event");
                        ExceptionUtility.handleException(e);
                    } finally {
                        lock.unlock();
                    }
                }
            }
        };
        executor.start();
    }

    protected void disconnect() {
        try {
            context.disconnect();
        } catch (Throwable e) {
        }
    }

    protected void clearTimer() {
        timer = 0;
    }

    protected void setTimer(long value) {
        timer = value + System.currentTimeMillis();
    }

    protected void setInActiveTimer() {
        setTimer(IAC_TIMEOUT);
//        timer = IAC_TIMEOUT - 2 * 1000 + random.nextInt(5) * 1000 + System.currentTimeMillis();
    }

    protected void changeState(FSMState newState) {
        getStates()[state.getCode()].exitAction();
        Logger.log(LoggerLevel.DEBUG, " State changed from " + state + " to " + newState);
        state = newState;
        getStates()[state.getCode()].entryAction();
    }

    protected DiameterMessage getMessage(StateEvent event) {
        return ((FSMEvent) event).getMessage();
    }

    public boolean handleEvent(StateEvent event) throws InternalException {
        if (executor == null)
            startProcess();

        Logger.log(LoggerLevel.DEBUG, "Handling event %s ", event.getType());

        boolean res = false;
        try {
            res = eventQueue.offer(event, 1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Logger.log(LoggerLevel.DEBUG, "Can not offer into queue");
        }

        if (!res) {
            throw new InternalException("FSM overloaded");
        }
        return true;
    }

    public FSMState getState() {
        return state;
    }

    protected State[] getStates() {

        if (states == null)
            states = new State[]{ //OPEN

                    new MachineState() {

                        public void entryAction() {
                            setInActiveTimer();
                            watchdogSent = false;
                        }

                        @Override
                        public boolean defaultAction(StateEvent event) {
                            switch (event.getType()) {
                                case DISCONNECT_EVENT:
                                    clearTimer();
                                    disconnect();
                                    changeState(FSMState.CLOSED);
                                    break;
                                case TIMEOUT_EVENT:
                                    try {
                                        context.sendDWR();
                                        setTimer(DWA_TIMEOUT);
                                        if (watchdogSent) {
                                            changeState(FSMState.SUSPECT);
                                        } else {
                                            watchdogSent = true;
                                        }
                                    } catch (Throwable e) {
                                        Logger.log(LoggerLevel.DEBUG, e, "Can not send DWR");
                                        disconnect();
                                        changeState(FSMState.CLOSED);
                                    }
                                    break;
                                case STOP_EVENT:
                                    try {
                                        int discCause = DiscCause.REBOOTING;
                                        if (event.getData() != null)
                                            discCause = (Integer) event.getData();
                                        context.sendDPR(discCause);
                                        setTimer(DPA_TIMEOUT);
                                        changeState(FSMState.CLOSING);
                                    } catch (Throwable e) {
                                        Logger.log(LoggerLevel.DEBUG, e, "Can not send DPR");
                                        disconnect();
                                        changeState(FSMState.CLOSED);
                                    }
                                    break;
                                case RECEIVE_MSG_EVENT:
                                    setInActiveTimer();
                                    try {
                                        context.receiveMessage(getMessage(event));
                                    } catch (ParseException e) {
                                        Logger.log(LoggerLevel.DEBUG, e, "Can not receive message");
                                    }
                                    break;
                                case DPR_EVENT:
                                    try {
                                        context.sendDPA(getMessage(event), ResultCode.SUCCESS, null);
                                    } catch (Throwable e) {
                                        Logger.log(LoggerLevel.DEBUG, e, "Can not send DPA");
                                    }
                                    disconnect();
                                    changeState(FSMState.CLOSED);
                                    break;
                                case DWR_EVENT:
                                    setInActiveTimer();
                                    try {
                                        context.sendDWA(getMessage(event), ResultCode.SUCCESS, null);
                                    } catch (Throwable e) {
                                        Logger.log(LoggerLevel.DEBUG, e, "Can not send DWA");
                                        disconnect();
                                        changeState(FSMState.CLOSED);
                                    }
                                    break;
                                case DWA_EVENT:
                                    setInActiveTimer();
                                    watchdogSent = false;
                                    break;
                                case SEND_MSG_EVENT:
                                    try {
                                        context.sendMessage(getMessage(event));
                                    } catch (Throwable e) {
                                        Logger.log(LoggerLevel.DEBUG, e, "Can not send message");
                                        disconnect();
                                        changeState(FSMState.CLOSED);
                                    }
                                    break;
                                default:
                                    Logger.log(LoggerLevel.DEBUG, "Unknown event type: {} in state {}", event.getType(), state);
                                    return false;
                            }
                            return true;
                        }
                    },

                    new MachineState() // SUSPECT
                    {
                        public boolean defaultAction(StateEvent event) {
                            switch (event.getType()) {
                                case DISCONNECT_EVENT:
                                    clearTimer();
                                    disconnect();
                                    changeState(FSMState.CLOSED);
                                    break;
                                case TIMEOUT_EVENT:
                                    disconnect();
                                    changeState(FSMState.CLOSED);
                                    break;
                                case STOP_EVENT:
                                    try {
                                        int discCause = DiscCause.REBOOTING;
                                        if (event.getData() != null)
                                            discCause = (Integer) event.getData();
                                        context.sendDPR(discCause);
                                        setInActiveTimer();
                                        changeState(FSMState.CLOSING);
                                    } catch (Throwable e) {
                                        Logger.log(LoggerLevel.DEBUG, e, "Can not send DPR");
                                        disconnect();
                                        changeState(FSMState.CLOSED);
                                    }
                                    break;
                                case DPR_EVENT:
                                    try {
                                        context.sendDPA(getMessage(event), ResultCode.SUCCESS, null);
                                    } catch (Throwable e) {
                                        Logger.log(LoggerLevel.DEBUG, e, "Can not send DPA");
                                    }
                                    disconnect();
                                    changeState(FSMState.CLOSED);
                                    break;
                                case DWA_EVENT:
                                    changeState(FSMState.OPEN);
                                    break;
                                case DWR_EVENT:
                                    try {
                                        context.sendDWA(getMessage(event), ResultCode.SUCCESS, null);
                                        changeState(FSMState.OPEN);
                                    } catch (Throwable e) {
                                        Logger.log(LoggerLevel.DEBUG, e, "Can not send DWA");
                                        disconnect();
                                        changeState(FSMState.CLOSED);
                                    }
                                    break;
                                case RECEIVE_MSG_EVENT:
                                    try {
                                        context.receiveMessage(getMessage(event));
                                    } catch (ParseException e) {
                                        Logger.log(LoggerLevel.DEBUG, e, "Can not receive message");
                                    }
                                    changeState(FSMState.OPEN);
                                    break;
                                case SEND_MSG_EVENT:
                                    throw new RuntimeException("Connection is down");
                                default:
                                    Logger.log(LoggerLevel.DEBUG, "Unknown event type: %s in state %s", event.getType(), state);
                                    return false;
                            }
                            return true;
                        }
                    },

                    new MachineState() // CLOSED
                    {
                        public void entryAction() {
                            clearTimer();
                            executor = null;
                        }

                        public boolean defaultAction(StateEvent event) {
                            switch (event.getType()) {
                                case START_EVENT:
                                    try {
                                        context.connect();
                                        context.sendCER();
                                        setTimer(CEA_TIMEOUT);
                                        changeState(FSMState.WAITING);
                                    } catch (Throwable e) {
                                        Logger.log(LoggerLevel.DEBUG, e, "Connect error");
                                        disconnect();
                                        changeState(FSMState.CLOSED);
                                    }
                                    break;
                                case SEND_MSG_EVENT:
                                    throw new RuntimeException("Connection is down");
                                case STOP_EVENT:
                                case DISCONNECT_EVENT:
                                    break;
                                default:
                                    Logger.log(LoggerLevel.DEBUG, "Unknown event type: %s in state %s", event.getType(), state);
                                    return false;
                            }
                            return true;
                        }
                    },

                    new MachineState() // WAITING
                    {
                        public void entryAction() {
                            setTimer(CEA_TIMEOUT);
                        }

                        public boolean defaultAction(StateEvent event) {
                            switch (event.getType()) {
                                case DISCONNECT_EVENT:
                                case TIMEOUT_EVENT:
                                case STOP_EVENT:
                                    //clearTimer();
                                    //disconnect();
                                    //changeState(FSMState.CLOSED);
                                    break;
                                case CEA_EVENT:
                                    clearTimer();
                                    try {
                                        if (context.processCEA(((FSMEvent) event).getKey(), getMessage(event))) {
                                            changeState(FSMState.OPEN);
                                        } else {
                                            clearTimer();
                                            disconnect();
                                            changeState(FSMState.CLOSED);
                                        }
                                    } catch (ParseException e) {
                                        Logger.log(LoggerLevel.DEBUG, "Can not process CEA event");
                                    }
                                    break;
                                case SEND_MSG_EVENT:
                                    throw new RuntimeException("Connection is down");
                                default:
                                    Logger.log(LoggerLevel.DEBUG, "Unknown event type: %s in state %s", event.getType(), state);
                                    return false;
                            }
                            return true;
                        }
                    },

                    new MachineState() // CLOSING
                    {
                        public boolean defaultAction(StateEvent event) {
                            switch (event.getType()) {
                                case TIMEOUT_EVENT:
                                case DPA_EVENT:
                                    disconnect();
                                    changeState(FSMState.CLOSED);
                                    break;
                                case RECEIVE_MSG_EVENT:
                                    try {
                                        context.receiveMessage(getMessage(event));
                                    } catch (ParseException e) {

                                    }
                                    break;
                                case SEND_MSG_EVENT:
                                    throw new RuntimeException("Stack now is stopping");
                                case STOP_EVENT:
                                case DISCONNECT_EVENT:
                                    disconnect();
                                    break;
                                default:
                                    Logger.log(LoggerLevel.DEBUG, "Unknown event type: %s in state %s", event.getType(), state);
                                    return false;
                            }
                            return true;
                        }
                    },

            };

        return states;
    }

    private abstract class MachineState implements State {

        @Override
        public void entryAction() {

        }

        @Override
        public void exitAction() {

        }
    }

}
