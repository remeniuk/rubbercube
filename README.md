rubbercube
==========

Library for OLAP over ElasticSearch. Provides efficient aggreagation and analysis over hige fact tables at a real-time. 

    resolvers += "rubbercube" at "https://bokland.github.io/rubbercube"

    libraryDependencies += "com.bokland" %% "rubbercube" % "0.2-SNAPSHOT"

## API
All queries are passed as JSON URL-encoded strings to q GET-parameter.
The description below describes format for different query types by giving examples of JSON documents with several placeholders:

* `"value1" | "value2" | "value3"` — put here one of the values.
* `{{placeholderName}}` — placeholder for some arbitrary value, like object id, number or other query parameter, names usually describe meaning of the value. All values are usually strings, but sometimes type is denoted as suffix after colon (like {{interval:number}}).
All such placeholders are required by default, optional placeholders are denoted with question mark (e.g. {{cubeId}}?), if such optional placeholder is omitted, whole field should be removed from a query.
* `RawCamelCaseName` — class name for some substructure to be put at the given place, see substructure description below.
* `[Placeholder, ...]` — the value in placeholder forms a JSON array, i.e. can be repeated several times in usual JSON-manner.

There are several special cases, edge cases etc, they are described in plain text in Javascript-style comments (e.g. “// this field makes sense for some types only”).

All the rest text in descriptions should be passed as is.

Example (it's not a real query, I made it up for illustration only!):
```javascript
{
    "type": "sliceAndDice" | "leftJoin",
    "cubeId": {{cubeId}},
    "queries": [Query, ...], // only for "type": "leftJoin"
    "alias": {{alias}}?,
    "interval": {{interval:number}}
}
```
means:
* "type" field can be one of "sliceAndDice" or "leftJoin" strings,
* "cubeId" is some arbitrary cube id string value and is required,
* "queries" field is a JSON array of objects of class Query, and makes sense only if "type" field has "leftJoin" value (according to comment),
* "alias" is an optional alias string, and "interval" is some number value and is required.

## Classes description

### SliceAndDice Query
```javascript
{
    "type": "sliceAndDice",
    "cube": {{cubeId}},
    "aggregations": [Aggregation, ...],
    "measures": [Measure, ...],
    "filters": [Filter, ...],
    "parent_id" {{parentId}}?,
    "from": {{from:int}}?,
    "size": {{size:int}}?,
    "include_fields": [{{fieldName}}, ...]?,
    "exclude_fields": [{{fieldName}}, ...]?,
    "sort": [
        [{{fieldName}}, "asc" | "desc"], ...
    ]?
}
```

### LeftJoin Query
```javascript
{
    "type": "leftJoin",
    "queries": [Query, ...],
    "by": [Dimension, ...],
    "measures": [Measure, ...]
}
```

### Aggregation
```javascript
{
    "dimension": Dimension,
    "aggregation": AggregationType
}
```

### Dimension
```javascript
{
    "field": {{fieldName}},
    "cubeId": {{cubeId}}?,
    "alias": {{alias}}?
}
```

### AggregationType
```javascript
{
    "type": "number" | "date" | "category" | "missing",
    "date_type": "Day" | "Week" | "Month" | "Quarter" | "Year", // for "type": "date"
    "interval": {{interval:number}} // for "type": "number"
}
```

### Measure
There're three types of measure:
```javascript
{
    "type": "reference",
    "alias": {{alias}}
}
```
```javascript
{
    "type": "dimension",
    "alias": {{alias}}?,
    "operation": "countdistinct" | "count" | "sum" | "avg" | "max" | "min" | "categories"
}
```
```javascript
{
    "type": "derived",
    "alias": {{alias}}?,
    "operation": "div",
    "dim1": Measure,
    "dim2": Measure
}
```

### Filter

Filters are the special case, as their structure highly depends on filter type, though they can be split up into several groups by their kind.

#### Single dimension filters
```javascript
{
    "operation": "eql" | "neql" | "gt" | "gte" | "lt" | "lte", // "dimension" equals (==), not equals (!=), greater than (>), greater or equal than (>=), less than (<), less or equal than (<=) "value"
    "dimension": Dimension,
    "value": {{value}}
}
```
```javascript
{
    "operation": "in", // "dimension" is equal to one of "value"s in the array
    "dimension": Dimension,
    "value": [{{value}}, ...]
}
```

#### Multi-dimensional filters
```javascript
{
    "operation": "and" | "or", // all (logical AND) / any (logic OR) "filters" are true
    "filters" [Filter, ...]
}
```

#### Zero dimension filters
```javascript
{
    "operation": "missing" | "exists", // "dimension" is missing / exists in a document
    "dimension": Dimension
}
```
