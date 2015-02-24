package com.kodekutters.ripple.examples

import com.kodekutters.ripple.protocol._
import play.api.libs.json._


object BasicTest {

  def main(args: Array[String]): Unit = {

    test6

  }

  def test0 = {

    import com.kodekutters.ripple.protocol.Response._

    val js = Json.parse( """{
                       "id" : 123,
                            "result" : {
                              "account_data" : {
                                "Account" : "rKQsDnGTkc3532Zp7pX6zaLZz48xJBqkJL",
                                "Balance" : "140000002",
                                "Flags" : 0,
                                "LedgerEntryType" : "AccountRoot",
                                "OwnerCount" : 0,
                                "PreviousTxnID" : "0769D7ED4125192DEB1EFADA639A756B88481012E34DE9020DA502DEE771C83A",
                                "PreviousTxnLgrSeq" : 7261800,
                                "Sequence" : 1,
                                "index" : "00B650A19D8F07674F1E83661006077E01AE7BD4FAAF3782868EECF0E7EEA747"
                              },
                              "ledger_index" : 11804921,
                              "validated" : true
                            },
                            "status" : "success",
                            "type" : "response"
                          }""")

    println("\n js: " + Json.prettyPrint(js))

    val response = Json.fromJson[Response](js)
    println("\n Response: " + response + "\n")

    val rjs = Json.toJson(response.get)
    println("\n rjs: " + Json.prettyPrint(rjs))

  }

  def test1 = {

    import com.kodekutters.ripple.protocol.Response._

    val js2 = Json.parse( """ {
                               "id" : 456,
                               "result" : {
                                 "account" : "rKQsDnGTkc3532Zp7pX6zaLZz48xJBqkJL",
                                 "lines" : [ ],
                                 "ledger_current_index" : 11823483,
                                 "validated" : false
                               },
                               "status" : "success",
                               "type" : "response"
                             }""")

    val js1 = Json.parse( """ {
                               "id" : 456,
                               "result" : {
                                 "account" : "rKQsDnGTkc3532Zp7pX6zaLZz48xJBqkJL",
                                 "ledger_current_index" : 11823483,
                                 "lines" : [ {
                                   "account" : "r3vi7mWxru9rJCxETCyA1CHvzL96eZWx5z",
                                   "balance" : "0",
                                   "currency" : "ASP",
                                   "limit" : "0",
                                   "limit_peer" : "10",
                                   "no_ripple" : true,
                                   "no_ripple_peer" : false,
                                   "quality_in" : 0,
                                   "quality_out" : 0
                                 } ],
                                 "validated" : false
                               },
                               "status" : "success",
                               "type" : "response"
                             }""")

    val js3 = Json.parse( """{
                                "id": 1,
                                "status": "success",
                                "type": "response",
                                "result": {
                                    "account": "r9cZA1mLK5R5Am25ArfXFmqgNwjZgnfk59",
                                    "lines": [
                                        {
                                            "account": "r3vi7mWxru9rJCxETCyA1CHvzL96eZWx5z",
                                            "balance": "0",
                                            "currency": "ASP",
                                            "limit": "0",
                                            "limit_peer": "10",
                                            "quality_in": 0,
                                            "quality_out": 0
                                        },
                                        {
                                            "account": "r3vi7mWxru9rJCxETCyA1CHvzL96eZWx5z",
                                            "balance": "0",
                                            "currency": "XAU",
                                            "limit": "0",
                                            "limit_peer": "0",
                                            "no_ripple": true,
                                            "no_ripple_peer": true,
                                            "quality_in": 0,
                                            "quality_out": 0
                                        },
                                        {
                                            "account": "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q",
                                            "balance": "3.497605752725159",
                                            "currency": "USD",
                                            "limit": "5",
                                            "limit_peer": "0",
                                            "no_ripple": true,
                                            "quality_in": 0,
                                            "quality_out": 0
                                        }
                                    ]
                               }
                           }""")

    println("\n js3: " + Json.prettyPrint(js3))

    val response3 = Json.fromJson[Response](js3)
    println("\n Response3: " + response3 + "\n")

    val rjs3 = Json.toJson(response3.get)
    println("\n rjs3: " + Json.prettyPrint(rjs3))

  }

  def test2() = {

    val t = Json.parse( """{"account":"rKQsDnGTkc3532Zp7pX6zaLZz48xJBqkJL","lines":[ {
                                                             "account" : "r3vi7mWxru9rJCxETCyA1CHvzL96eZWx5z",
                                                             "balance" : "0",
                                                             "currency" : "ASP",
                                                             "limit" : "0",
                                                             "limit_peer" : "10",
                         "no_ripple" : true,
                         "no_ripple_peer" : false,
                                                             "quality_in" : 0,
                                                             "quality_out" : 0
                                                           } ],"ledger_current_index":11823483,"validated":false} """)

    println("\n t: " + Json.prettyPrint(t))

    val x = Json.fromJson[ResponseType](t)
    println("\n x: " + x + "\n")

    val z = x.get.asInstanceOf[Account_lines_response]

    z.lines.foreach(l => println("l: " + l))

  }

  def test3 = {

    import com.kodekutters.ripple.protocol.Response._

    val jss = Json.parse( """ {
                               "id" : 456,
                               "status": "success",
                                "type": "response",
                               "result" : {
                                 "account" : "rKQsDnGTkc3532Zp7pX6zaLZz48xJBqkJL",
                                 "ledger_current_index" : 118483,
                                 "lines" : [                                 {
                                                                        "account": "r3vi7mWxru9rJCxETCyA1CHvzL96eZWx5z",
                                                                        "balance": "0",
                                                                        "currency": "ASP",
                                                                        "limit": "0",
                                                                        "limit_peer": "10",
                                                                        "quality_in": 0,
                                                                        "quality_out": 0
                                                                    },
                                                                    {
                                   "account" : "r3vi7mWxru9rJCxETCyA1CHvzL96eZWx5z",
                                   "balance" : "0",
                                   "currency" : "ASP",
                                   "limit" : "0",
                                   "limit_peer" : "10",
                                   "no_ripple" : true,
                                   "no_ripple_peer" : false,
                                   "quality_in" : 0,
                                   "quality_out" : 0
                                 },
                                                                    {
                                                                        "account": "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q",
                                                                        "balance": "3.497605752725159",
                                                                        "currency": "USD",
                                                                        "limit": "5",
                                                                        "limit_peer": "0",
                                                                        "quality_in": 0,
                                                                        "quality_out": 0
                                                                    } ],
                           "ledger_index" : 11804921,
                                 "validated" : false
                               }
                             }""")


    
    
    val js = Json.parse("""{
                                 "id": 1,
                                 "status": "success",
                                 "type": "response",
                                 "result": {
                                     "account": "r9cZA1mLK5R5Am25ArfXFmqgNwjZgnfk59",
                                     "lines": [
                                         {
                                             "account": "r3vi7mWxru9rJCxETCyA1CHvzL96eZWx5z",
                                             "balance": "0",
                                             "currency": "ASP",
                                             "limit": "0",
                                             "limit_peer": "10",
                                             "quality_in": 0,
                                             "quality_out": 0
                                         },
                                         {
                                             "account": "r3vi7mWxru9rJCxETCyA1CHvzL96eZWx5z",
                                             "balance": "0",
                                             "currency": "XAU",
                                             "limit": "0",
                                             "limit_peer": "0",
                                             "no_ripple": true,
                                             "no_ripple_peer": true,
                                             "quality_in": 0,
                                             "quality_out": 0
                                         },
                                         {
                                             "account": "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q",
                                             "balance": "3.497605752725159",
                                             "currency": "USD",
                                             "limit": "5",
                                             "limit_peer": "0",
                                             "no_ripple": true,
                                             "quality_in": 0,
                                             "quality_out": 0
                                         }
                                     ]
                                 }
                             }""")
    
    
    println("\n js: " + Json.prettyPrint(js))

    val response = Json.fromJson[Response](js)
    println("\n Response: " + response + "\n")


    println("\n response.get.result: " + response.get.result + "\n")

    response.get.result.get.asInstanceOf[Account_lines_response].lines.foreach(l => println("l: " + l))

    val rjs = Json.toJson(response.get)
    println("\n rjs: " + Json.prettyPrint(rjs))


  }
  
  def test5 = {
    
    val js = Json.parse("""{
                             "id": 2,
                             "status": "success",
                             "type": "response",
                             "result": {
                               "account": "r9cZA1mLK5R5Am25ArfXFmqgNwjZgnfk59",
                               "offers": [
                                 {
                                   "flags": 0,
                                   "seq": 1399,
                                   "taker_gets": "16666666",
                                   "taker_pays": {
                                     "currency": "XAU",
                                     "issuer": "rs9M85karFkCRjvc6KMWn8Coigm9cbcgcx",
                                     "value": "0.0001"
                                   }
                                 }
                               ]
                             }
                           }""")

    println("\n js: " + Json.prettyPrint(js))

    val response = Json.fromJson[Response](js)
    println("\n Response: " + response + "\n")


    println("\n response.get.result: " + response.get.result + "\n")

    response.get.result.get.asInstanceOf[Account_offers_response].offers.foreach(l => println("offer: " + l))

  }

  def test6 = {

    import com.kodekutters.ripple.protocol.RequestType._

    val js = Json.parse("""{
                             "id": 2,
                             "command": "account_tx",
                             "account": "r9cZA1mLK5R5Am25ArfXFmqgNwjZgnfk59",
                             "ledger_index_min": -1,
                             "ledger_index_max": -1,
                             "binary": false,
                             "limit": 2,
                             "forward": false
                           }""")

    println("\n js: " + Json.prettyPrint(js))

    val request = Json.fromJson[RequestType](js)
    println("\n request: " + request + "\n")

    println("\n requestjs: " + Json.prettyPrint(Json.toJson(request.get)))

  }
  
  def test7 = {
    
    val js = Json.parse("""{
                             "id" : 2,
                             "result" : {
                               "account" : "r9cZA1mLK5R5Am25ArfXFmqgNwjZgnfk59",
                               "ledger_index_max" : 11844412,
                               "ledger_index_min" : 32570,
                               "limit" : 2,
                               "marker" : {
                                 "ledger" : 10470825,
                                 "seq" : 2
                               },
                               "transactions" : [ {
                                 "meta" : {
                                   "AffectedNodes" : [ {
                                     "DeletedNode" : {
                                       "FinalFields" : {
                                         "Account" : "rhU6iBSedmXwAFfsr3YeMSXmGnxLJFx3JB",
                                         "BookDirectory" : "DFA3B6DDAB58C7E8E5D944E736DA4B7046C30E4F460FD9DE4D08594FC79E1600",
                                         "BookNode" : "0000000000000000",
                                         "Flags" : 131072,
                                         "OwnerNode" : "0000000000000031",
                                         "PreviousTxnID" : "83CA9AB231B0B4DA8ACF6305A6D7B00AB83404A1FDB8F8BCF7108EB87E0A8196",
                                         "PreviousTxnLgrSeq" : 10555009,
                                         "Sequence" : 12370,
                                         "TakerGets" : "44978350398",
                                         "TakerPays" : {
                                           "currency" : "USD",
                                           "issuer" : "rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B",
                                           "value" : "1056.990784602728"
                                         }
                                       },
                                       "LedgerEntryType" : "Offer",
                                       "LedgerIndex" : "0496450E3F46368FB011B8B524605A906C0854441D30420457A81EA89BE649BE"
                                     }
                                   }, {
                                     "ModifiedNode" : {
                                       "FinalFields" : {
                                         "Balance" : {
                                           "currency" : "USD",
                                           "issuer" : "rrrrrrrrrrrrrrrrrrrrBZbvji",
                                           "value" : "0"
                                         },
                                         "Flags" : 1114112,
                                         "HighLimit" : {
                                           "currency" : "USD",
                                           "issuer" : "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q",
                                           "value" : "0"
                                         },
                                         "HighNode" : "0000000000000000",
                                         "LowLimit" : {
                                           "currency" : "USD",
                                           "issuer" : "r9cZA1mLK5R5Am25ArfXFmqgNwjZgnfk59",
                                           "value" : "5"
                                         },
                                         "LowNode" : "0000000000000000"
                                       },
                                       "LedgerEntryType" : "RippleState",
                                       "LedgerIndex" : "2DECFAC23B77D5AEA6116C15F5C6D4669EBAEE9E7EE050A40FE2B1E47B6A9419",
                                       "PreviousFields" : {
                                         "Balance" : {
                                           "currency" : "USD",
                                           "issuer" : "rrrrrrrrrrrrrrrrrrrrBZbvji",
                                           "value" : "3"
                                         }
                                       },
                                       "PreviousTxnID" : "6F35AD78AA196389D15F4BAF054122070506633C1506EF16A48877E2593CCE2D",
                                       "PreviousTxnLgrSeq" : 10555014
                                     }
                                   }, {
                                     "ModifiedNode" : {
                                       "FinalFields" : {
                                         "Balance" : {
                                           "currency" : "USD",
                                           "issuer" : "rrrrrrrrrrrrrrrrrrrrBZbvji",
                                           "value" : "1228.52678703286"
                                         },
                                         "Flags" : 1114112,
                                         "HighLimit" : {
                                           "currency" : "USD",
                                           "issuer" : "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q",
                                           "value" : "0"
                                         },
                                         "HighNode" : "0000000000000076",
                                         "LowLimit" : {
                                           "currency" : "USD",
                                           "issuer" : "rwfUHgzJ39V71nUxUNo38Twxg1SAmvZRV6",
                                           "value" : "50000"
                                         },
                                         "LowNode" : "0000000000000000"
                                       },
                                       "LedgerEntryType" : "RippleState",
                                       "LedgerIndex" : "4AB2FFDEE65E025AAB54305A161C80D32082574BB0502311F29227089C696388",
                                       "PreviousFields" : {
                                         "Balance" : {
                                           "currency" : "USD",
                                           "issuer" : "rrrrrrrrrrrrrrrrrrrrBZbvji",
                                           "value" : "1225.52678703286"
                                         }
                                       },
                                       "PreviousTxnID" : "65C5A92212AA82A89C3824F6F071FE49C95C45DE9113EB51763A217DBACB5B4C",
                                       "PreviousTxnLgrSeq" : 10554856
                                     }
                                   }, {
                                     "ModifiedNode" : {
                                       "FinalFields" : {
                                         "Flags" : 0,
                                         "Owner" : "rMjkX36HHyazqoUmxjwoZW6AMsvh9Jrr97",
                                         "RootIndex" : "65E53030C59CD541BB6A8B631F13FA9BBF6F6E28B63D41AA363F23313B34B094"
                                       },
                                       "LedgerEntryType" : "DirectoryNode",
                                       "LedgerIndex" : "65E53030C59CD541BB6A8B631F13FA9BBF6F6E28B63D41AA363F23313B34B094"
                                     }
                                   }, {
                                     "ModifiedNode" : {
                                       "FinalFields" : {
                                         "Account" : "rhU6iBSedmXwAFfsr3YeMSXmGnxLJFx3JB",
                                         "Balance" : "335297994919",
                                         "Flags" : 0,
                                         "OwnerCount" : 12,
                                         "Sequence" : 12379
                                       },
                                       "LedgerEntryType" : "AccountRoot",
                                       "LedgerIndex" : "6ACA1AAB7CD8877EA63E0C282A37FC212AD24640743F019AC2DD8674E003B741",
                                       "PreviousFields" : {
                                         "OwnerCount" : 13
                                       },
                                       "PreviousTxnID" : "83CA9AB231B0B4DA8ACF6305A6D7B00AB83404A1FDB8F8BCF7108EB87E0A8196",
                                       "PreviousTxnLgrSeq" : 10555009
                                     }
                                   }, {
                                     "ModifiedNode" : {
                                       "FinalFields" : {
                                         "Balance" : {
                                           "currency" : "USD",
                                           "issuer" : "rrrrrrrrrrrrrrrrrrrrBZbvji",
                                           "value" : "-15.68270575272517"
                                         },
                                         "Flags" : 131072,
                                         "HighLimit" : {
                                           "currency" : "USD",
                                           "issuer" : "r9cZA1mLK5R5Am25ArfXFmqgNwjZgnfk59",
                                           "value" : "5000"
                                         },
                                         "HighNode" : "0000000000000000",
                                         "LowLimit" : {
                                           "currency" : "USD",
                                           "issuer" : "rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B",
                                           "value" : "0"
                                         },
                                         "LowNode" : "000000000000004A"
                                       },
                                       "LedgerEntryType" : "RippleState",
                                       "LedgerIndex" : "826CF5BFD28F3934B518D0BDF3231259CBD3FD0946E3C3CA0C97D2C75D2D1A09",
                                       "PreviousFields" : {
                                         "Balance" : {
                                           "currency" : "USD",
                                           "issuer" : "rrrrrrrrrrrrrrrrrrrrBZbvji",
                                           "value" : "-12.68270575272517"
                                         }
                                       },
                                       "PreviousTxnID" : "6F35AD78AA196389D15F4BAF054122070506633C1506EF16A48877E2593CCE2D",
                                       "PreviousTxnLgrSeq" : 10555014
                                     }
                                   }, {
                                     "ModifiedNode" : {
                                       "FinalFields" : {
                                         "Flags" : 0,
                                         "IndexPrevious" : "000000000000002C",
                                         "Owner" : "rhU6iBSedmXwAFfsr3YeMSXmGnxLJFx3JB",
                                         "RootIndex" : "A93C6B313E260AC7C7734DF44F4461075E2C937C936C2B81DA2C9F69D4A0B0F2"
                                       },
                                       "LedgerEntryType" : "DirectoryNode",
                                       "LedgerIndex" : "8D8FB3359BBA810ED5C5894088F2415E322811181ADCC5BB087E829207DFBBEB"
                                     }
                                   }, {
                                     "ModifiedNode" : {
                                       "FinalFields" : {
                                         "Account" : "rMjkX36HHyazqoUmxjwoZW6AMsvh9Jrr97",
                                         "Balance" : "25116082528",
                                         "Flags" : 0,
                                         "OwnerCount" : 5,
                                         "Sequence" : 2197
                                       },
                                       "LedgerEntryType" : "AccountRoot",
                                       "LedgerIndex" : "8F55B7E947241AD38FD6D47374BF8E7CA7DF177C8B79712B4CAC5E91FD5023FF",
                                       "PreviousFields" : {
                                         "OwnerCount" : 6
                                       },
                                       "PreviousTxnID" : "0327747C391C678CA2AC46F422E1E8D307A41E9C0ED5DB2677B51CEBE41BD243",
                                       "PreviousTxnLgrSeq" : 10554892
                                     }
                                   }, {
                                     "ModifiedNode" : {
                                       "FinalFields" : {
                                         "Account" : "rafTUepKMRP7Xf7B3LAyXt6bHVT16cKBnw",
                                         "Balance" : "233929959",
                                         "Flags" : 0,
                                         "OwnerCount" : 14,
                                         "Sequence" : 31927
                                       },
                                       "LedgerEntryType" : "AccountRoot",
                                       "LedgerIndex" : "A27BB98F7C9D32F404B364622645F80480F87C8A91BB13CA9F6E569144C2A5A8",
                                       "PreviousFields" : {
                                         "Balance" : "233941959",
                                         "Sequence" : 31926
                                       },
                                       "PreviousTxnID" : "65C5A92212AA82A89C3824F6F071FE49C95C45DE9113EB51763A217DBACB5B4C",
                                       "PreviousTxnLgrSeq" : 10554856
                                     }
                                   }, {
                                     "DeletedNode" : {
                                       "FinalFields" : {
                                         "ExchangeRate" : "4D08594FC79E1600",
                                         "Flags" : 0,
                                         "RootIndex" : "DFA3B6DDAB58C7E8E5D944E736DA4B7046C30E4F460FD9DE4D08594FC79E1600",
                                         "TakerGetsCurrency" : "0000000000000000000000000000000000000000",
                                         "TakerGetsIssuer" : "0000000000000000000000000000000000000000",
                                         "TakerPaysCurrency" : "0000000000000000000000005553440000000000",
                                         "TakerPaysIssuer" : "0A20B3C85F482532A9578DBB3950B85CA06594D1"
                                       },
                                       "LedgerEntryType" : "DirectoryNode",
                                       "LedgerIndex" : "DFA3B6DDAB58C7E8E5D944E736DA4B7046C30E4F460FD9DE4D08594FC79E1600"
                                     }
                                   }, {
                                     "DeletedNode" : {
                                       "FinalFields" : {
                                         "ExchangeRate" : "4D08594FE7353EDE",
                                         "Flags" : 0,
                                         "RootIndex" : "DFA3B6DDAB58C7E8E5D944E736DA4B7046C30E4F460FD9DE4D08594FE7353EDE",
                                         "TakerGetsCurrency" : "0000000000000000000000000000000000000000",
                                         "TakerGetsIssuer" : "0000000000000000000000000000000000000000",
                                         "TakerPaysCurrency" : "0000000000000000000000005553440000000000",
                                         "TakerPaysIssuer" : "0A20B3C85F482532A9578DBB3950B85CA06594D1"
                                       },
                                       "LedgerEntryType" : "DirectoryNode",
                                       "LedgerIndex" : "DFA3B6DDAB58C7E8E5D944E736DA4B7046C30E4F460FD9DE4D08594FE7353EDE"
                                     }
                                   }, {
                                     "ModifiedNode" : {
                                       "FinalFields" : {
                                         "Balance" : {
                                           "currency" : "USD",
                                           "issuer" : "rrrrrrrrrrrrrrrrrrrrBZbvji",
                                           "value" : "-2818.268620725051"
                                         },
                                         "Flags" : 2228224,
                                         "HighLimit" : {
                                           "currency" : "USD",
                                           "issuer" : "rafTUepKMRP7Xf7B3LAyXt6bHVT16cKBnw",
                                           "value" : "50000"
                                         },
                                         "HighNode" : "0000000000000000",
                                         "LowLimit" : {
                                           "currency" : "USD",
                                           "issuer" : "rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B",
                                           "value" : "0"
                                         },
                                         "LowNode" : "00000000000001B0"
                                       },
                                       "LedgerEntryType" : "RippleState",
                                       "LedgerIndex" : "FAD28E839AF29C6CCB8DA6DC71510A5BF8A9C34062C128071C7D22E2469B8288",
                                       "PreviousFields" : {
                                         "Balance" : {
                                           "currency" : "USD",
                                           "issuer" : "rrrrrrrrrrrrrrrrrrrrBZbvji",
                                           "value" : "-2821.274620725051"
                                         }
                                       },
                                       "PreviousTxnID" : "65C5A92212AA82A89C3824F6F071FE49C95C45DE9113EB51763A217DBACB5B4C",
                                       "PreviousTxnLgrSeq" : 10554856
                                     }
                                   }, {
                                     "DeletedNode" : {
                                       "FinalFields" : {
                                         "Account" : "rMjkX36HHyazqoUmxjwoZW6AMsvh9Jrr97",
                                         "BookDirectory" : "DFA3B6DDAB58C7E8E5D944E736DA4B7046C30E4F460FD9DE4D08594FE7353EDE",
                                         "BookNode" : "0000000000000000",
                                         "Flags" : 0,
                                         "OwnerNode" : "0000000000000000",
                                         "PreviousTxnID" : "0327747C391C678CA2AC46F422E1E8D307A41E9C0ED5DB2677B51CEBE41BD243",
                                         "PreviousTxnLgrSeq" : 10554892,
                                         "Sequence" : 2196,
                                         "TakerGets" : "4255320000",
                                         "TakerPays" : {
                                           "currency" : "USD",
                                           "issuer" : "rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B",
                                           "value" : "100"
                                         }
                                       },
                                       "LedgerEntryType" : "Offer",
                                       "LedgerIndex" : "FB2E442ED1A5BCA1E237BA133807AE17AED1A7E4B9F404906308ADB01A57609D"
                                     }
                                   } ],
                                   "DeliveredAmount" : {
                                     "currency" : "USD",
                                     "issuer" : "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q",
                                     "value" : "3"
                                   },
                                   "TransactionIndex" : 5,
                                   "TransactionResult" : "tesSUCCESS",
                                   "delivered_amount" : {
                                     "currency" : "USD",
                                     "issuer" : "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q",
                                     "value" : "3"
                                   }
                                 },
                                 "tx" : {
                                   "Account" : "rafTUepKMRP7Xf7B3LAyXt6bHVT16cKBnw",
                                   "Amount" : {
                                     "currency" : "USD",
                                     "issuer" : "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q",
                                     "value" : "8"
                                   },
                                   "Destination" : "rwfUHgzJ39V71nUxUNo38Twxg1SAmvZRV6",
                                   "Fee" : "12000",
                                   "Flags" : 2147876864,
                                   "LastLedgerSequence" : 10555021,
                                   "Paths" : [ [ {
                                     "account" : "rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B",
                                     "type" : 1,
                                     "type_hex" : "0000000000000001"
                                   }, {
                                     "account" : "r9cZA1mLK5R5Am25ArfXFmqgNwjZgnfk59",
                                     "type" : 1,
                                     "type_hex" : "0000000000000001"
                                   }, {
                                     "account" : "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q",
                                     "type" : 1,
                                     "type_hex" : "0000000000000001"
                                   } ], [ {
                                     "account" : "rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B",
                                     "type" : 1,
                                     "type_hex" : "0000000000000001"
                                   }, {
                                     "currency" : "XRP",
                                     "type" : 16,
                                     "type_hex" : "0000000000000010"
                                   }, {
                                     "currency" : "USD",
                                     "issuer" : "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q",
                                     "type" : 48,
                                     "type_hex" : "0000000000000030"
                                   }, {
                                     "account" : "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q",
                                     "type" : 1,
                                     "type_hex" : "0000000000000001"
                                   } ], [ {
                                     "account" : "rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B",
                                     "type" : 1,
                                     "type_hex" : "0000000000000001"
                                   }, {
                                     "currency" : "USD",
                                     "issuer" : "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q",
                                     "type" : 48,
                                     "type_hex" : "0000000000000030"
                                   }, {
                                     "account" : "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q",
                                     "type" : 1,
                                     "type_hex" : "0000000000000001"
                                   } ], [ {
                                     "account" : "rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B",
                                     "type" : 1,
                                     "type_hex" : "0000000000000001"
                                   }, {
                                     "currency" : "XRP",
                                     "type" : 16,
                                     "type_hex" : "0000000000000010"
                                   }, {
                                     "currency" : "USD",
                                     "issuer" : "rfsEoNBUBbvkf4jPcFe2u9CyaQagLVHGfP",
                                     "type" : 48,
                                     "type_hex" : "0000000000000030"
                                   }, {
                                     "account" : "rfsEoNBUBbvkf4jPcFe2u9CyaQagLVHGfP",
                                     "type" : 1,
                                     "type_hex" : "0000000000000001"
                                   }, {
                                     "account" : "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q",
                                     "type" : 1,
                                     "type_hex" : "0000000000000001"
                                   } ] ],
                                   "SendMax" : {
                                     "currency" : "USD",
                                     "issuer" : "rafTUepKMRP7Xf7B3LAyXt6bHVT16cKBnw",
                                     "value" : "8.165909964"
                                   },
                                   "Sequence" : 31926,
                                   "SigningPubKey" : "02FE003812C9380EBEC93EA51F8082EE752B70AEC97EE134EC506FB4054E2DA1DA",
                                   "TransactionType" : "Payment",
                                   "TxnSignature" : "304402204A3CBC5D078C2DA3A167A57DA3D9B1CCC5DA1E3FE78D49715959AC6101CB8EDF0220115C7A54429C19222AE038410C0185224E24647981B4819505F3AA86CE8EC62A",
                                   "date" : 472065700,
                                   "hash" : "6FE8C824364FB1195BCFEDCB368DFEE3980F7F78D3BF4DC4174BB4C86CF8C5CE",
                                   "inLedger" : 10555014,
                                   "ledger_index" : 10555014
                                 },
                                 "validated" : true
                               }, {
                                 "meta" : {
                                   "AffectedNodes" : [ {
                                     "ModifiedNode" : {
                                       "FinalFields" : {
                                         "Account" : "rBHMbioz9znTCqgjZ6Nx43uWY43kToEPa9",
                                         "Balance" : "207740037",
                                         "Flags" : 0,
                                         "OwnerCount" : 12,
                                         "Sequence" : 39934
                                       },
                                       "LedgerEntryType" : "AccountRoot",
                                       "LedgerIndex" : "0A089B976836170F770D03BD9A401979071D00EC3F3E3A5D0B69ACFD7A5A8EE6",
                                       "PreviousFields" : {
                                         "Balance" : "207752037",
                                         "Sequence" : 39933
                                       },
                                       "PreviousTxnID" : "1F1304C002F5ABC4ABB11C71FC7690E54850FF84AED86277B1C849FCE2AB584C",
                                       "PreviousTxnLgrSeq" : 10554932
                                     }
                                   }, {
                                     "ModifiedNode" : {
                                       "FinalFields" : {
                                         "Balance" : {
                                           "currency" : "USD",
                                           "issuer" : "rrrrrrrrrrrrrrrrrrrrBZbvji",
                                           "value" : "3"
                                         },
                                         "Flags" : 1114112,
                                         "HighLimit" : {
                                           "currency" : "USD",
                                           "issuer" : "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q",
                                           "value" : "0"
                                         },
                                         "HighNode" : "0000000000000000",
                                         "LowLimit" : {
                                           "currency" : "USD",
                                           "issuer" : "r9cZA1mLK5R5Am25ArfXFmqgNwjZgnfk59",
                                           "value" : "5"
                                         },
                                         "LowNode" : "0000000000000000"
                                       },
                                       "LedgerEntryType" : "RippleState",
                                       "LedgerIndex" : "2DECFAC23B77D5AEA6116C15F5C6D4669EBAEE9E7EE050A40FE2B1E47B6A9419",
                                       "PreviousFields" : {
                                         "Balance" : {
                                           "currency" : "USD",
                                           "issuer" : "rrrrrrrrrrrrrrrrrrrrBZbvji",
                                           "value" : "5"
                                         }
                                       },
                                       "PreviousTxnID" : "241FA771AE5C5F303BA844C8B740785AE3D3B9E1645846BC0A2F27EF01514793",
                                       "PreviousTxnLgrSeq" : 10470825
                                     }
                                   }, {
                                     "ModifiedNode" : {
                                       "FinalFields" : {
                                         "Balance" : {
                                           "currency" : "USD",
                                           "issuer" : "rrrrrrrrrrrrrrrrrrrrBZbvji",
                                           "value" : "-12.68270575272517"
                                         },
                                         "Flags" : 131072,
                                         "HighLimit" : {
                                           "currency" : "USD",
                                           "issuer" : "r9cZA1mLK5R5Am25ArfXFmqgNwjZgnfk59",
                                           "value" : "5000"
                                         },
                                         "HighNode" : "0000000000000000",
                                         "LowLimit" : {
                                           "currency" : "USD",
                                           "issuer" : "rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B",
                                           "value" : "0"
                                         },
                                         "LowNode" : "000000000000004A"
                                       },
                                       "LedgerEntryType" : "RippleState",
                                       "LedgerIndex" : "826CF5BFD28F3934B518D0BDF3231259CBD3FD0946E3C3CA0C97D2C75D2D1A09",
                                       "PreviousFields" : {
                                         "Balance" : {
                                           "currency" : "USD",
                                           "issuer" : "rrrrrrrrrrrrrrrrrrrrBZbvji",
                                           "value" : "-10.68270575272517"
                                         }
                                       },
                                       "PreviousTxnID" : "241FA771AE5C5F303BA844C8B740785AE3D3B9E1645846BC0A2F27EF01514793",
                                       "PreviousTxnLgrSeq" : 10470825
                                     }
                                   }, {
                                     "ModifiedNode" : {
                                       "FinalFields" : {
                                         "Balance" : {
                                           "currency" : "USD",
                                           "issuer" : "rrrrrrrrrrrrrrrrrrrrBZbvji",
                                           "value" : "-304.8320677756126"
                                         },
                                         "Flags" : 2228224,
                                         "HighLimit" : {
                                           "currency" : "USD",
                                           "issuer" : "r4X3WWZ3UZMDw3Z7T32FXK2NAaiitSWZ9c",
                                           "value" : "100000"
                                         },
                                         "HighNode" : "0000000000000000",
                                         "LowLimit" : {
                                           "currency" : "USD",
                                           "issuer" : "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q",
                                           "value" : "0"
                                         },
                                         "LowNode" : "000000000000009B"
                                       },
                                       "LedgerEntryType" : "RippleState",
                                       "LedgerIndex" : "E6F28B26ABD8A728D0F7FEBF28BE850978ED186357F590009236C6AB139DF5DE",
                                       "PreviousFields" : {
                                         "Balance" : {
                                           "currency" : "USD",
                                           "issuer" : "rrrrrrrrrrrrrrrrrrrrBZbvji",
                                           "value" : "-302.8320677756126"
                                         }
                                       },
                                       "PreviousTxnID" : "2A332ABB56CA17BC97E7EDAA5044F7C352C4A82A874D9F1151C2438C70D2057F",
                                       "PreviousTxnLgrSeq" : 10554923
                                     }
                                   }, {
                                     "ModifiedNode" : {
                                       "FinalFields" : {
                                         "Balance" : {
                                           "currency" : "USD",
                                           "issuer" : "rrrrrrrrrrrrrrrrrrrrBZbvji",
                                           "value" : "-906.1384685187613"
                                         },
                                         "Flags" : 2228224,
                                         "HighLimit" : {
                                           "currency" : "USD",
                                           "issuer" : "rBHMbioz9znTCqgjZ6Nx43uWY43kToEPa9",
                                           "value" : "10000"
                                         },
                                         "HighNode" : "0000000000000000",
                                         "LowLimit" : {
                                           "currency" : "USD",
                                           "issuer" : "rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B",
                                           "value" : "0"
                                         },
                                         "LowNode" : "00000000000001F5"
                                       },
                                       "LedgerEntryType" : "RippleState",
                                       "LedgerIndex" : "F0759F8CE9DA3266C173E69AC1157AEA348A035F789033DB6650379A95A5C7F7",
                                       "PreviousFields" : {
                                         "Balance" : {
                                           "currency" : "USD",
                                           "issuer" : "rrrrrrrrrrrrrrrrrrrrBZbvji",
                                           "value" : "-908.1424685187613"
                                         }
                                       },
                                       "PreviousTxnID" : "0837E33D6AB9CF9F4862A4D813BC387FA7138773D7877F67549B849523DC5673",
                                       "PreviousTxnLgrSeq" : 10554921
                                     }
                                   } ],
                                   "TransactionIndex" : 4,
                                   "TransactionResult" : "tesSUCCESS",
                                   "delivered_amount" : {
                                     "currency" : "USD",
                                     "issuer" : "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q",
                                     "value" : "2"
                                   }
                                 },
                                 "tx" : {
                                   "Account" : "rBHMbioz9znTCqgjZ6Nx43uWY43kToEPa9",
                                   "Amount" : {
                                     "currency" : "USD",
                                     "issuer" : "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q",
                                     "value" : "2"
                                   },
                                   "Destination" : "r4X3WWZ3UZMDw3Z7T32FXK2NAaiitSWZ9c",
                                   "Fee" : "12000",
                                   "Flags" : 2147876864,
                                   "LastLedgerSequence" : 10555021,
                                   "Paths" : [ [ {
                                     "account" : "rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B",
                                     "type" : 1,
                                     "type_hex" : "0000000000000001"
                                   }, {
                                     "account" : "r9cZA1mLK5R5Am25ArfXFmqgNwjZgnfk59",
                                     "type" : 1,
                                     "type_hex" : "0000000000000001"
                                   }, {
                                     "account" : "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q",
                                     "type" : 1,
                                     "type_hex" : "0000000000000001"
                                   } ], [ {
                                     "account" : "rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B",
                                     "type" : 1,
                                     "type_hex" : "0000000000000001"
                                   }, {
                                     "currency" : "XRP",
                                     "type" : 16,
                                     "type_hex" : "0000000000000010"
                                   }, {
                                     "currency" : "USD",
                                     "issuer" : "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q",
                                     "type" : 48,
                                     "type_hex" : "0000000000000030"
                                   }, {
                                     "account" : "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q",
                                     "type" : 1,
                                     "type_hex" : "0000000000000001"
                                   } ], [ {
                                     "account" : "rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B",
                                     "type" : 1,
                                     "type_hex" : "0000000000000001"
                                   }, {
                                     "currency" : "USD",
                                     "issuer" : "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q",
                                     "type" : 48,
                                     "type_hex" : "0000000000000030"
                                   }, {
                                     "account" : "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q",
                                     "type" : 1,
                                     "type_hex" : "0000000000000001"
                                   } ], [ {
                                     "account" : "rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B",
                                     "type" : 1,
                                     "type_hex" : "0000000000000001"
                                   }, {
                                     "currency" : "XRP",
                                     "type" : 16,
                                     "type_hex" : "0000000000000010"
                                   }, {
                                     "currency" : "USD",
                                     "issuer" : "rfsEoNBUBbvkf4jPcFe2u9CyaQagLVHGfP",
                                     "type" : 48,
                                     "type_hex" : "0000000000000030"
                                   }, {
                                     "account" : "rfsEoNBUBbvkf4jPcFe2u9CyaQagLVHGfP",
                                     "type" : 1,
                                     "type_hex" : "0000000000000001"
                                   }, {
                                     "account" : "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q",
                                     "type" : 1,
                                     "type_hex" : "0000000000000001"
                                   } ] ],
                                   "SendMax" : {
                                     "currency" : "USD",
                                     "issuer" : "rBHMbioz9znTCqgjZ6Nx43uWY43kToEPa9",
                                     "value" : "2.02404"
                                   },
                                   "Sequence" : 39933,
                                   "SigningPubKey" : "03DFEFC9A95AEF55232A2B89867745CE45373F5CE23C34D51D21343CEA92BD61AD",
                                   "TransactionType" : "Payment",
                                   "TxnSignature" : "304402201393C34D2F3DC3CEB28FB6014012DF21E7555F6217DFF4F63BE07D4A99A2EC5F022076DB1EFE8B776AF24A6D2CD2DC66EFF71A873058B829FAAE449649C846EBF25B",
                                   "date" : 472065700,
                                   "hash" : "6F35AD78AA196389D15F4BAF054122070506633C1506EF16A48877E2593CCE2D",
                                   "inLedger" : 10555014,
                                   "ledger_index" : 10555014
                                 },
                                 "validated" : true
                               } ]
                             },
                             "status" : "success",
                             "type" : "response"
                           }""")


  //  println("\n js: " + Json.prettyPrint(js))

    val response = Json.fromJson[Response](js)
    println("\n Response: " + response.get + "\n")

    val z = response.get.result.get.asInstanceOf[Account_tx_response]
    z.transactions.foreach(t => println("t: " + t))

  }


}
