//
//  Balance.swift
//  Cosmostation
//
//  Created by yongjoo on 20/03/2019.
//  Copyright © 2019 wannabit. All rights reserved.
//

import Foundation

public class Balance {
    var balance_id:Int64 = -1;
    var balance_account_id:Int64 = -1;
    var balance_denom: String = "";
    var balance_amount:String = "";
    var balance_fetch_time:Int64 = -1;
    
    var balance_frozen:String?
    var balance_locked:String?
    
    init(_ id:Int64, _ accout_id:Int64, _ demon:String, _ amount:String, _ fetch_time:Int64, _ frozen:String?, _ locked:String?) {
        self.balance_id = id;
        self.balance_account_id = accout_id;
        self.balance_denom = demon;
        self.balance_amount = amount;
        self.balance_fetch_time = fetch_time;
        if (frozen != nil) {
            self.balance_frozen = frozen;
        } else {
            self.balance_frozen = "0";
        }
        if (locked != nil) {
            self.balance_locked = locked;
        } else {
            self.balance_locked = "0";
        }
    }
    
    init(_ accout_id:Int64, _ demon:String, _ amount:String, _ fetch_time:Int64, _ frozen:String?, _ locked:String?) {
        self.balance_account_id = accout_id;
        self.balance_denom = demon;
        self.balance_amount = amount;
        self.balance_fetch_time = fetch_time;
        if (frozen != nil) {
            self.balance_frozen = frozen;
        } else {
            self.balance_frozen = "0";
        }
        if (locked != nil) {
            self.balance_locked = locked;
        } else {
            self.balance_locked = "0";
        }
    }
    
    init(_ accout_id:Int64, _ demon:String, _ amount:String, _ fetch_time:Int64) {
        self.balance_account_id = accout_id;
        self.balance_denom = demon;
        self.balance_amount = amount;
        self.balance_fetch_time = fetch_time;
    }
    
    func getAllAmountBnbToken() -> NSDecimalNumber {
        return WUtils.stringToDecimal(self.balance_amount).adding(WUtils.stringToDecimal(self.balance_locked))
    }
    
    func exchangeBnbValue(_ tic:NSMutableDictionary?) -> NSDecimalNumber {
        if (tic == nil || tic!.object(forKey: "lastPrice") == nil) {
            return NSDecimalNumber.zero
        }
        if (WUtils.isBnbMArketToken(self.balance_denom)) {
            return getAllAmountBnbToken().dividing(by: NSDecimalNumber(string: tic!.object(forKey: "lastPrice") as? String), withBehavior: WUtils.handler8)
        } else {
            return getAllAmountBnbToken().multiplying(by: NSDecimalNumber(string: tic!.object(forKey: "lastPrice") as? String), withBehavior: WUtils.handler8)
        }
    }
    
}


//public class Balance {
//    var balance_id:Int64 = -1;
//    var balance_account_id:Int64 = -1;
//    var balance_denom: String = "";
//    var balance_amount:NSDecimalNumber = NSDecimalNumber.zero;
//    var balance_fetch_time:Int64 = -1;
//
//    var balance_frozen:NSDecimalNumber?
//    var balance_locked:NSDecimalNumber?
//
//    init(_ id:Int64, _ accout_id:Int64, _ demon:String, _ amount:String, _ fetch_time:Int64, _ frozen:String?, _ locked:String?) {
//        self.balance_id = id;
//        self.balance_account_id = accout_id;
//        self.balance_denom = demon;
//        self.balance_amount = NSDecimalNumber(string: amount)
//        self.balance_fetch_time = fetch_time;
//        self.balance_frozen = NSDecimalNumber(string: frozen);
//        self.balance_locked = NSDecimalNumber(string: locked);
//    }
//
//    init(_ accout_id:Int64, _ demon:String, _ amount:String, _ fetch_time:Int64, _ frozen:String?, _ locked:String?) {
//        self.balance_account_id = accout_id;
//        self.balance_denom = demon;
//        self.balance_amount = NSDecimalNumber(string: amount)
//        self.balance_fetch_time = fetch_time;
//        self.balance_frozen = NSDecimalNumber(string: frozen);
//        self.balance_locked = NSDecimalNumber(string: locked);
//    }
//
//    init(_ accout_id:Int64, _ demon:String, _ amount:String, _ fetch_time:Int64) {
//        self.balance_account_id = accout_id;
//        self.balance_denom = demon;
//        self.balance_amount = NSDecimalNumber(string: amount)
//        self.balance_fetch_time = fetch_time;
//    }
//
//}


