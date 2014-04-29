curl -XDELETE localhost:9200/rubbercube

curl -XPOST 'http://localhost:9200/rubbercube' -d '{
  "settings": {
    "index": {
      "mapping.allow_type_wrapper": true
    }
  }
}'

curl -XPUT localhost:9200/rubbercube/user/1 -d '{
    "country" : "US",
    "gender" : "Female",
    "source": "Organic"
}'

curl -XPUT localhost:9200/rubbercube/user/2 -d '{
    "country" : "GB",
    "gender" : "Male",
    "source": "Ads"
}'

curl -XPUT localhost:9200/rubbercube/purchase/_mapping -d '{
    "purchase": {
      "_parent" : {
          "type" : "user"
      }
    } 
}'

curl -XPUT localhost:9200/rubbercube/purchase/1?parent=1 -d '{
    "date" : "2014-01-01T00:00:00",    
    "country" : "US",
    "gender" : "Female",    
    "amount" : 1.99   
}'

curl -XPUT localhost:9200/rubbercube/purchase/2?parent=1 -d '{
    "date" : "2014-01-02T00:00:00",    
    "country" : "US",
    "gender" : "Female",    
    "amount" : 4.99      
}'

curl -XPUT localhost:9200/rubbercube/purchase/3?parent=2 -d '{
    "date" : "2014-01-01T00:00:00",    
    "country" : "GB",
    "gender" : "Male",    
    "amount" : 19.99    
}'

curl -XPUT localhost:9200/rubbercube/purchase/4?parent=2 -d '{
    "date" : "2014-01-03T00:00:00",    
    "country" : "GB",
    "gender" : "Male",    
    "amount" : 99.99      
}'

curl -XPUT localhost:9200/rubbercube/purchase/5?parent=1 -d '{
    "date" : "2014-01-02T00:00:00",    
    "country" : "US",
    "gender" : "Female",    
    "amount" : 1.99      
}'

