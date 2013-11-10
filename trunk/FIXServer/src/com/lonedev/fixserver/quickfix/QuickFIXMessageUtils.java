package com.lonedev.fixserver.quickfix;

import java.util.Calendar;
import quickfix.Message;
import quickfix.field.Account;
import quickfix.field.ClOrdID;
import quickfix.field.HandlInst;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.SecurityID;
import quickfix.field.SecurityIDSource;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TransactTime;

public class QuickFIXMessageUtils {

    public static Message createFIX44NewOrderSingle(String clientOrderId, Side side, String account, String cusip, double quantity) {
        quickfix.Message newOrder = new quickfix.fix44.NewOrderSingle(new ClOrdID(clientOrderId), side, new TransactTime(Calendar.getInstance().getTime()), new OrdType(OrdType.MARKET));
        newOrder.setField(new HandlInst(HandlInst.MANUAL_ORDER));
        newOrder.setField(new Account(account));
        newOrder.setField(new Symbol(cusip));
        newOrder.setField(new SecurityID(cusip));
        newOrder.setField(new SecurityIDSource(SecurityIDSource.CUSIP));
        newOrder.setField(new OrderQty(quantity));
        
        return newOrder;
    }

}
