package com.lonedev.fixserver;

import quickfix.*;

public class QuickFIXApplicationAdapter implements quickfix.Application {

    @Override
    public void onCreate(SessionID sid) { }

    @Override
    public void onLogon(SessionID sid) { }

    @Override
    public void onLogout(SessionID sid) { }

    @Override
    public void toAdmin(Message msg, SessionID sid) { }

    @Override
    public void fromAdmin(Message msg, SessionID sid) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon { }

    @Override
    public void toApp(Message msg, SessionID sid) throws DoNotSend { }

    @Override
    public void fromApp(Message msg, SessionID sid) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType { }
    
}
