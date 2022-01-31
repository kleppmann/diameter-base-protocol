package com.linkedlogics.diameter.object;


import com.linkedlogics.diameter.exception.TransportException;

import java.io.IOException;
import java.net.InetAddress;

public interface AvpCode {
    int ACCESS_NETWORK_CHARGING_IDENTIFIER_VALUE = 503;
    int ACCESS_NETWORK_INFORMATION = 1263;
    int ACCUMULATED_COST = 2052;
    int ADAPTATIONS = 1217;
    int ADDITIONAL_CONTENT_INFORMATION = 1207;
    int ADDITIONAL_TYPE_INFORMATION = 1205;
    int ADDRESS_DATA = 897;
    int ADDRESS_DOMAIN = 898;
    int ADDRESSEE_TYPE = 1208;
    int ADDRESS_TYPE = 899;
    int AF_CHARGING_IDENTIFIER = 505;
    int AF_CORRELATION_INFORMATION = 1276;
    int ALLOCATION_RETENTION_PRIORITY = 1034;
    int ALTERNATE_CHARGED_PARTY_ADDRESS = 1280;
    int APN_AGGREGATE_MAX_BITRATE_DL = 1040;
    int APN_AGGREGATE_MAX_BITRATE_UL = 1041;
    int AOC_COST_INFORMATION = 2053;
    int AOC_INFORMATION = 2054;
    int AOC_REQUEST_TYPE = 2055;
    int APPLICATION_PROVIDED_CALLED_PARTY_ADDRESS = 837;
    int APPLICATION_SERVER = 836;
    int APPLICATION_SERVER_ID = 2101;
    int APPLICATION_SERVER_INFORMATION = 850;
    int APPLICATION_SERVICE_TYPE = 2102;
    int APPLICATION_SESSION_ID = 2103;
    int APPLIC_ID = 1218;
    int ASSOCIATED_PARTY_ADDRESS = 2035;
    int ASSOCIATED_URI = 856;
    int AUTHORIZED_QOS = 849;
    int AUX_APPLIC_INFO = 1219;
    int BASE_TIME_INTERVAL = 1265;
    int BEARER_IDENTIFIER = 1020;
    int BEARER_SERVICE = 854;
    int CALLED_ASSERTED_IDENTITY = 1250;
    int CALLED_PARTY_ADDRESS = 832;
    int CALLING_PARTY_ADDRESS = 831;
    int CARRIER_SELECT_ROUTING_INFORMATION = 2023;
    int CAUSE_CODE = 861;
    int CG_ADDRESS = 846;
    int CHANGE_CONDITION = 2037;
    int CHANGE_TIME = 2038;
    int CHARGED_PARTY = 857;
    int CHARGING_RULE_BASE_NAME = 1004;
    int CLASS_IDENTIFIER = 1214;
    int CLIENT_ADDRESS = 2018;
    int CONTENT_CLASS = 1220;
    int CONTENT_DISPOSITION = 828;
    int CONTENT_ID = 2116;
    int CONTENT_PROVIDER_ID = 2117;
    int CONTENT_LENGTH = 827;
    int CONTENT_SIZE = 1206;
    int CONTENT_TYPE = 826;
    int CURRENT_TARIFF = 2056;
    int DATA_CODING_SCHEME = 2001;
    int DCD_INFORMATION = 2115;
    int DEFERRED_LOCATION_EVENT_TYPE = 1230;
    int DELIVERY_REPORT_REQUESTED = 1216;
    int DELIVERY_STATUS = 2104;
    int DESTINATION_INTERFACE = 2002;
    int DIAGNOSTICS = 2039;
    int DOMAIN_NAME = 1200;
    int DRM_CONTENT = 1221;
    int DYNAMIC_ADDRESS_FLAG = 2051;
    int EARLY_MEDIA_DESCRIPTION = 1272;
    int ENVELOPE = 1266;
    int ENVELOPE_END_TIME = 1267;
    int ENVELOPE_REPORTING = 1268;
    int ENVELOPE_START_TIME = 1269;
    int EVENT = 825;
    int EVENT_CHARGING_TIMESTAMP = 1258;
    int EVENT_TYPE = 823;
    int EXPIRES = 888;
    int FILE_REPAIR_SUPPORTED = 1224;
    int FLOWS = 510;
    int GGSN_ADDRESS = 847;
    int GUARANTEED_BITRATE_UL = 1026;
    int GUARANTEED_BITRATE_DL = 1025;
    int IM_INFORMATION = 2110;
    int IMS_CHARGING_IDENTIFIER = 841;
    int IMS_COMMUNICATION_SERVICE_IDENTIFIER = 1281;
    int IMS_INFORMATION = 876;
    int INCOMING_TRUNK_GROUP_ID = 852;
    int INCREMENTAL_COST = 2062;
    int INTERFACE_ID = 2003;
    int INTERFACE_PORT = 2004;
    int INTERFACE_TEXT = 2005;
    int INTERFACE_TYPE = 2006;
    int INTER_OPERATOR_IDENTIFIER = 838;
    int LCS_CLIENT_DIALED_BY_MS = 1233;
    int LCS_CLIENT_EXTERNAL_ID = 1234;
    int LCS_CLIENT_ID = 1232;
    int LCS_CLIENT_TYPE = 1241;
    int LCS_DATA_CODING_SCHEME = 1236;
    int LCS_FORMAT_INDICATOR = 1237;
    int LCS_INFORMATION = 878;
    int LCS_NAME_STRING = 1238;
    int LCS_REQUESTOR_ID = 1239;
    int LCS_REQUESTOR_ID_STRING = 1240;
    int LOCAL_SEQUENCE_NUMBER = 2063;
    int LOCATION_ESTIMATE = 1242;
    int LOCATION_ESTIMATE_TYPE = 1243;
    int LOCATION_TYPE = 1244;
    int MAX_REQUESTED_BANDWIDTH_DL = 515;
    int MAX_REQUESTED_BANDWIDTH_UL = 516;
    int MBMS_2G_3G_INDICATOR = 907;
    int MBMS_INFORMATION = 880;
    int MBMS_SERVICE_AREA = 903;
    int MBMS_SERVICE_TYPE = 906;
    int MBMS_SESSION_IDENTITY = 908;
    int MBMS_USER_SERVICE_TYPE = 1225;
    int MEDIA_INITIATOR_FLAG = 882;
    int MEDIA_INITIATOR_PARTY = 1288;
    int MESSAGE_BODY = 889;
    int MESSAGE_CLASS = 1213;
    int MESSAGE_ID = 1210;
    int MESSAGE_SIZE = 1212;
    int MESSAGE_TYPE = 1211;
    int MMBOX_STORAGE_REQUESTED = 1248;
    int MM_CONTENT_TYPE = 1203;
    int MMS_INFORMATION = 877;
    int MMTEL_INFORMATION = 2030;
    int MSISDN = 701;
    int NEXT_TARIFF = 2057;
    int NODE_FUNCTIONALITY = 862;
    int NODE_ID = 2064;
    int NUMBER_OF_DIVERSIONS = 2034;
    int NUMBER_OF_MESSAGES_SENT = 2019;
    int NUMBER_OF_MESSAGES_SUCCESSFULLY_EXPLODED = 2111;
    int NUMBER_OF_MESSAGES_SUCCESSFULLY_SENT = 2112;
    int NUMBER_OF_PARTICIPANTS = 885;
    int NUMBER_OF_RECEIVED_TALK_BURSTS = 1282;
    int NUMBER_OF_TALK_BURSTS = 1283;
    int NUMBER_PORTABILITY_ROUTING_INFORMATION = 2024;
    int OFFLINE_CHARGING = 1278;
    int ONLINE_CHARGING_FLAG = 2303;
    int ORIGINATING_IOI = 839;
    int ORIGINATOR_SCCP_ADDRESS = 2008;
    int ORIGINATOR = 864;
    int ORIGINATOR_ADDRESS = 886;
    int ORIGINATOR_RECEIVED_ADDRESS = 2027;
    int ORIGINATOR_INTERFACE = 2009;
    int OUTGOING_TRUNK_GROUP_ID = 853;
    int PARTICIPANT_ACCESS_PRIORITY = 1259;
    int PARTICIPANT_ACTION_TYPE = 2049;
    int PARTICIPANT_GROUP = 1260;
    int PARTICIPANTS_INVOLVED = 887;
    int PDG_ADDRESS = 895;
    int PDG_CHARGING_ID = 896;
    int PDN_CONNECTION_ID = 2050;
    int PDP_ADDRESS = 1227;
    int PDP_CONTEXT_TYPE = 1247;
    int POC_CHANGE_CONDITION = 1261;
    int POC_CHANGE_TIME = 1262;
    int POC_CONTROLLING_ADDRESS = 858;
    int POC_EVENT_TYPE = 2025;
    int POC_GROUP_NAME = 859;
    int POC_INFORMATION = 879;
    int POC_SERVER_ROLE = 883;
    int POC_SESSION_ID = 1229;
    int POC_SESSION_INITIATION_TYPE = 1277;
    int POC_SESSION_TYPE = 884;
    int POC_USER_ROLE = 1252;
    int POC_USER_ROLE_IDS = 1253;
    int POC_USER_ROLE_INFO_UNITS = 1254;
    int POSITIONING_DATA = 1245;
    int PRIORITY = 1209;
    int PRIORITY_LEVEL = 1046;
    int PS_APPEND_FREE_FORMAT_DATA = 867;
    int PS_FREE_FORMAT_DATA = 866;
    int PS_FURNISH_CHARGING_INFORMATION = 865;
    int PS_INFORMATION = 874;
    int QOS_INFORMATION = 1016;
    int QOS_CLASS_IDENTIFIER = 1028;
    int QUOTA_CONSUMPTION_TIME = 881;
    int QUOTA_HOLDING_TIME = 871;
    int RAI = 909;
    int RATE_ELEMENT = 2058;
    int READ_REPLY_REPORT_REQUESTED = 1222;
    int RECEIVED_TALK_BURST_TIME = 1284;
    int RECEIVED_TALK_BURST_VOLUME = 1285;
    int RECIPIENT_ADDRESS = 1201;
    int RECIPIENT_INFO = 2026;
    int RECIPIENT_RECEIVED_ADDRESS = 2028;
    int RECIPIENT_SCCP_ADDRESS = 2010;
    int REFUND_INFORMATION = 2022;
    int REMAINING_BALANCE = 2021;
    int REPLY_APPLIC_ID = 1223;
    int REPLY_PATH_REQUESTED = 2011;
    int REPORTING_REASON = 872;
    int REQUESTED_PARTY_ADDRESS = 1251;
    int REQUIRED_MBMS_BEARER_CAPABILITIES = 901;
    int ROLE_OF_NODE = 829;
    int SCALE_FACTOR = 2059;
    int SDP_ANSWER_TIMESTAMP = 1275;
    int SDP_MEDIA_COMPONENT = 843;
    int SDP_MEDIA_DESCRIPTION = 845;
    int SDP_MEDIA_NAME = 844;
    int SDP_OFFER_TIMESTAMP = 1274;
    int SDP_SESSION_DESCRIPTION = 842;
    int SDP_TIMESTAMPS = 1273;
    int SDP_TYPE = 2036;
    int SERVED_PARTY_IP_ADDRESS = 848;
    int SERVICE_DATA_CONTAINER = 2040;
    int SERVICE_GENERIC_INFORMATION = 1256;
    int SERVICE_IDENTIFIER = 855;
    int SERVICE_INFORMATION = 873;
    int SERVICE_MODE = 2032;
    int SERVICE_SPECIFIC_DATA = 863;
    int SERVICE_SPECIFIC_INFO = 1249;
    int SERVICE_SPECIFIC_TYPE = 1257;
    int SERVING_NODE_TYPE = 2047;
    int SERVICE_TYPE = 2031;
    int SGSN_ADDRESS = 1228;
    int SGW_CHANGE = 2064;
    int SIP_METHOD = 824;
    int SIP_REQUEST_TIMESTAMP_FRACTION = 2301;
    int SIP_REQUEST_TIMESTAMP = 834;
    int SIP_RESPONSE_TIMESTAMP_FRACTION = 2302;
    int SIP_RESPONSE_TIMESTAMP = 835;
    int SM_DISCHARGE_TIME = 2012;
    int SM_MESSAGE_TYPE = 2007;
    int SM_PROTOCOL_ID = 2013;
    int SMSC_ADDRESS = 2017;
    int SMS_INFORMATION = 2000;
    int SMS_NODE = 2016;
    int SM_SERVICE_TYPE = 2029;
    int SM_STATUS = 2014;
    int SM_USER_DATA_HEADER = 2015;
    int START_TIME = 2041;
    int STOP_TIME = 2042;
    int SUBMISSION_TIME = 1202;
    int SUBSCRIBER_ROLE = 2033;
    int SUPPLEMENTARY_SERVICE = 2048;
    int TALK_BURST_EXCHANGE = 1255;
    int TALK_BURST_TIME = 1286;
    int TALK_BURST_VOLUME = 1287;
    int TARIFF_INFORMATION = 2060;
    int TERMINAL_INFORMATION = 1401;
    int TERMINATING_IOI = 840;
    int TIME_FIRST_USAGE = 2043;
    int TIME_LAST_USAGE = 2044;
    int TIME_QUOTA_MECHANISM = 1270;
    int TIME_QUOTA_THRESHOLD = 868;
    int TIME_QUOTA_TYPE = 1271;
    int TIME_STAMPS = 833;
    int TIME_USAGE = 2045;
    int TMGI = 900;
    int TOKEN_TEXT = 1215;
    int TOTAL_NUMBER_OF_MESSAGES_EXPLODED = 2113;
    int TOTAL_NUMBER_OF_MESSAGES_SENT = 2114;
    int TRAFFIC_DATA_VOLUMES = 2046;
    int TRIGGER = 1264;
    int TRIGGER_TYPE = 870;
    int TRUNK_GROUP_ID = 851;
    int TYPE_NUMBER = 1204;
    int UNIT_COST = 2061;
    int UNIT_QUOTA_THRESHOLD = 1226;
    int USER_DATA_RORF = 606;
    int USER_PARTICIPATING_TYPE = 1279;
    int USER_SESSION_ID = 830;
    int VAS_ID = 1102;
    int VASP_ID = 1101;
    int VOLUME_QUOTA_THRESHOLD = 869;
    int WAG_ADDRESS = 890;
    int WAG_PLMN_ID = 891;
    int WLAN_INFORMATION = 875;
    int WLAN_RADIO_CONTAINER = 892;
    int WLAN_SESSION_ID = 1246;
    int WLAN_TECHNOLOGY = 893;
    int WLAN_UE_LOCAL_IPADDRESS = 894;

    int ACCOUNTING_REALTIME_REQUIRED = 483;
    int AUTH_REQUEST_TYPE = 274;
    int AUTHORIZATION_LIFETIME = 291;
    int AUTH_GRACE_PERIOD = 276;
    int AUTH_SESSION_STATE = 277;
    int CLASS = 25;
    int E2E_SEQUENCE_AVP = 300;
    int ERROR_REPORTING_HOST = 294;
    int EVENT_TIMESTAMP = 55;
    int FAILED_AVP = 279;
    int ACCT_INTERIM_INTERVAL = 85;
    int USER_NAME = 1;
    int RESULT_CODE = 268;
    int EXPERIMENTAL_RESULT = 297;
    int EXPERIMENTAL_RESULT_CODE = 298;
    int TERMINATION_CAUSE = 295;
    int FIRMWARE_REVISION = 267;
    int HOST_IP_ADDRESS = 257;
    int MULTI_ROUND_TIMEOUT = 272;
    int ORIGIN_HOST = 264;
    int ORIGIN_REALM = 296;
    int ORIGIN_STATE_ID = 278;
    int REDIRECT_HOST = 292;
    int REDIRECT_HOST_USAGE = 261;
    int REDIRECT_MAX_CACHE_TIME = 262;
    int PRODUCT_NAME = 269;
    int SESSION_ID = 263;
    int SESSION_TIMEOUT = 27;
    int SESSION_BINDING = 270;
    int SESSION_SERVER_FAILOVER = 271;
    int DESTINATION_HOST = 293;
    int DESTINATION_REALM = 283;
    int ROUTE_RECORD = 282;
    int PROXY_INFO = 284;
    int PROXY_HOST = 280;
    int PROXY_STATE = 33;
    int AUTH_APPLICATION_ID = 258;
    int ACCT_APPLICATION_ID = 259;
    int INBAND_SECURITY_ID = 299;
    int VENDOR_ID = 266;
    int SUPPORTED_VENDOR_ID = 265;
    int VENDOR_SPECIFIC_APPLICATION_ID = 260;
    int RE_AUTH_REQUEST_TYPE = 285;
    int ACC_RECORD_TYPE = 480;
    int ACC_RECORD_NUMBER = 485;
    int ACC_SESSION_ID = 44;
    int ACC_SUB_SESSION_ID = 287;
    int ACC_MULTI_SESSION_ID = 50;
    int DISCONNECT_CAUSE = 273;
    int ERROR_MESSAGE = 281;
    int CC_CORRELATION_ID = 411;
    int CC_INPUT_OCTETS = 412;
    int CC_MONEY = 413;
    int CC_OUTPUT_OCTETS = 414;
    int CC_REQUEST_NUMBER = 415;
    int CC_REQUEST_TYPE = 416;
    int CC_SERVICE_SPECIFIC_UNITS = 417;
    int CC_SESSION_FAILOVER = 418;
    int CC_SUB_SESSION_ID = 419;
    int CC_TIME = 420;
    int CC_TOTAL_OCTETS = 421;
    int CC_UNIT_TYPE = 454;
    int CHECK_BALANCE_RESULT = 422;
    int COST_INFORMATION = 423;
    int COST_UNIT = 424;
    int CURRENCY_CODE = 425;
    int CREDIT_CONTROL = 426;
    int CREDIT_CONTROL_FAILURE_HANDLING = 427;
    int DIRECT_DEBITING_FAILURE_HANDLING = 428;
    int EXPONENT = 429;
    int FINAL_UNIT_ACTION = 449;
    int FINAL_UNIT_INDICATION = 430;
    int GRANTED_SERVICE_UNIT = 431;
    int GSU_POOL_ID = 453;
    int GSU_POOL_REFERENCE = 457;
    int MULTIPLE_SERVICES_CREDIT_CONTROL = 456;
    int MULTIPLE_SERVICES_INDICATOR = 455;
    int RATING_GROUP = 432;
    int REDIRECT_ADDRESS_TYPE = 433;
    int REDIRECT_SERVER = 434;
    int REDIRECT_ADDRESS = 435;
    int REQUESTED_ACTION = 436;
    int REQUESTED_SERVICE_UNIT = 437;
    int RESTRICTION_FILTER_RULE = 438;
    int SERVICE_CONTEXT_ID = 461;
    int SERVICE_IDENTIFIER_CCA = 439;
    int SERVICE_PARAMETER_INFO = 440;
    int SERVICE_PARAMETER_TYPE = 441;
    int SERVICE_PARAMETER_VALUE = 442;
    int SUBSCRIPTION_ID = 443;
    int SUBSCRIPTION_ID_DATA = 444;
    int SUBSCRIPTION_ID_TYPE = 450;
    int TARIFF_CHANGE_USAGE = 452;
    int TARIFF_TIME_CHANGE = 451;
    int UNIT_VALUE = 445;
    int USED_SERVICE_UNIT = 446;
    int USER_EQUIPMENT_INFO = 458;
    int USER_EQUIPMENT_INFO_TYPE = 459;
    int USER_EQUIPMENT_INFO_VALUE = 460;
    int VALUE_DIGITS = 447;
    int VALIDITY_TIME = 448;
    int VISITED_NETWORK_ID = 600;
    int PUBLIC_IDENTITY = 601;
    int SERVER_NAME = 602;
    int SERVER_CAPABILITIES = 603;
    int MANDATORY_CAPABILITY = 604;
    int OPTIONAL_CAPABILITY = 605;
    int USER_DATA_CXDX = 606;
    int SIP_NUMBER_AUTH_ITEMS = 607;
    int SIP_AUTHENTICATION_SCHEME = 608;
    int SIP_AUTHENTICATE = 609;
    int SIP_AUTHORIZATION = 610;
    int SIP_AUTHENTICATION_CONTEXT = 611;
    int SIP_AUTH_DATA_ITEM = 612;
    int SIP_ITEM_NUMBER = 613;
    int SERVER_ASSIGNMENT_TYPE = 614;
    int DEREGISTRATION_REASON = 615;
    int REASON_CODE = 616;
    int REASON_INFO = 617;
    int CHARGING_INFORMATION = 618;
    int PRI_EVENT_CHARGING_FUNCTION = 619;
    int SEC_EVENT_CHARGING_FUNCTION = 620;
    int PRI_CHARGING_COLLECTION_FUNCTION = 621;
    int SEC_CHARGING_COLLECTION_FUNCTION = 622;
    int USER_AUTORIZATION_TYPE = 623;
    int USER_DATA_ALREADY_AVAILABLE = 624;
    int CONFIDENTIALITY_KEY = 625;
    int INTEGRITY_KEY = 626;
    int SUPPORTED_FEATURES = 628;
    int FEATURE_LIST_ID = 629;
    int FEATURE_LIST = 630;
    int SUPPORTED_APPLICATIONS = 631;
    int ASSOCAITED_IDENTITIES = 632;
    int ORIGINATING_REQUEST = 633;
    int WILDCARDED_PSI = 634;
    int SIP_DIGEST_AUTHENTICATE = 635;
    int WILDCARDED_IMPU = 636;
    int UAR_FLAGS = 637;
    int LOOSE_ROUTE_INDICATION = 638;
    int SCSCF_RESTORATION_INFO = 639;
    int PATH = 640;
    int CONTACT = 641;
    int SUBSCRIPTION_INFO = 642;
    int CALL_ID_SIP_HEADER = 643;
    int FROM_SIP_HEADER = 644;
    int TO_SIP_HEADER = 645;
    int RECORD_ROUTE = 646;
    int ASSOCIATED_REGISTERED_IDENTITIES = 647;
    int MULTIPLE_REGISTRATION_INDICATION = 648;
    int RESTORATION_INFO = 649;
    int USER_IDENTITY = 700;
    int USER_DATA_SH = 702;
    int DATA_REFERENCE = 703;
    int SERVICE_INDICATION = 704;
    int SUBS_REQ_TYPE = 705;
    int REQUESTED_DOMAIN = 706;
    int CURRENT_LOCATION = 707;
    int IDENTITY_SET = 708;
    int EXPIRY_TIME = 709;
    int SEND_DATA_INDICATION = 710;
    int DSAI_TAG = 711;
    int LOW_BALANCE_INDICATION = 2020;
    int TGPP_CHARGING_CHARACTERISTICS = 13;
    int TGPP_CHARGING_ID = 2;
    int TGPP_GGSN_MCC_MNC = 9;
    int TGPP_IMSI = 1;
    int TGPP_IMSI_MCC_MNC = 8;
    int TGPP_MS_TIMEZONE = 23;
    int TGPP_NSAPI = 10;
    int TGPP_PDP_TYPE = 3;
    int TGPP_RAT_TYPE = 21;
    int TGPP_SELECTION_MODE = 12;
    int TGPP_SESSION_STOP_INDICATOR = 11;
    int GPP_SGSN_MCC_MNC = 18;
    int GPP_USER_LOCATION_INFO = 22;
    int TGPP2_BSID = 5535;
}