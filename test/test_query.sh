curl -XPOST localhost:9200/rubbercube/purchase/_search?pretty=true -d '{ 
    "size": 0,
    "query": {    
      "has_parent" : {
          "parent_type" : "user",
          "query" : {
              "match" : {
                  "source" : "Organic"
              }
          }
      }    
    },
    "aggs" : {
        "revenue_by_day" : {
            "date_histogram" : {
                "field" : "date",
                "interval" : "day"
            }, 
            "aggs" : {
        "sum_amount": {
          "sum" : {
            "field" : "amount"
          }
        },
        "payers_count": {
                  "cardinality" : {
                      "field" : "_parent",
                      "precision_threshold": 40000
                  }
        }                
                 
            }            
        }        
    }    
}'
