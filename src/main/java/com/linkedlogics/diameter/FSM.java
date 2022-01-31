package com.linkedlogics.diameter;

import java.util.logging.Level;
import java.util.logging.Logger;

public class FSM {
    private static Logger LOGGER = Logger.getLogger(FSM.class.getName());

    public enum State {
        NEW {
            @Override
            public State next(Transition transition) {
                return (transition == Transition.CREATE) ? RUNNING : ILLEGAL;
            }
        },
        RUNNING {
            @Override
            public State next(Transition transition) {
                if (transition == Transition.WAIT) {
                    return BLOCKED;
                } else if (transition == Transition.FINISH) {
                    return DEAD;
                } else {
                    return ILLEGAL;
                }
            }
        },
        BLOCKED {
            @Override
            public State next(Transition transition) {
                return (transition == Transition.RESUME) ? RUNNING : ILLEGAL;
            }
        },
        DEAD {
            @Override
            public State next(Transition transition) {
                return ILLEGAL;
            }
        },
        ILLEGAL {
            @Override
            public State next(Transition transition) {
                return ILLEGAL;
            }
        };

        public State next(Transition transition) {
            return null;
        }
    }

    public enum Transition {
        CREATE,
        WAIT,
        RESUME,
        FINISH
    }

    public static void main(String[] args) {
        State finish = run(State.NEW, Transition.CREATE, Transition.WAIT, Transition.RESUME, Transition.WAIT, Transition.RESUME, Transition.FINISH);
        System.out.println(finish);
    }

    public static State run(State start, Transition... transitions) {
        State state = start;

        LOGGER.log(Level.INFO, "start state: {0}", start);
        for (Transition transition : transitions) {
            state = state.next(transition);
            LOGGER.log(Level.INFO, "current state: {0}", state);
        }

        LOGGER.info("finished");

        return state;
    }
}
