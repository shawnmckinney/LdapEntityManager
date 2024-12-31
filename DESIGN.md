# Overview

Ldap Entry Manager (LEM) reads and writes records from an LDAP database. It uses LDAPv3 protocol for Create Read Update Delete (CRUD).
The mapping between the backend database and the entity that is exposed via the API is defined in Yet Another Markup Language (YAML) format.
There are two input files to the APIs. The first is the entity model which defines the mapping between the logical and physical format. 
The second contains the actual data using the logical definitions for referencing the physical data values.

## YAML model, entitity data and API
    - [see](./PROPOSAL.md)

## Details
- LEM uses Apache Directory LDAP API for data access. The configuration for target LDAP instance is stored in the config.properties file as name:value pairs.
- Apache Log4j2 is the used for logging. It uses a configuration file name log4j2.xml.
- The data input (model and entity) are stored in the test/conf folder.

## Design

LEM APIs are defined in the EntityMgr.java interface. There are two ways of interacting with them:

1. Passing YAML files as args
    - add( String modelFile, String dataFile, String className )
    - className is name of Java class name of the model and entity.
2. Passing Java objects as args
    - void add( Entity model, Entity data )

- #1 unmarshals the YAML into Java objects before calling 2. 
- #2 saves YAML processing steps and has Java objects as arguments.
- Both are functionally the same. Choose the one that works best for integration.

## Operations:

1. Create, Update or Delete
+ Input: 2 YAML files; model x, entity y 
+ process input data:
--> Unmarshal(x, y) -> Objects(x, y) 
--> Reflection(x, y) -> Map(key, list<A>) 
--> DAO (MAP) -> ldap op(attrs)

2. Read or Search 
+ Input: 1 YAML files; model x, 1 key 
+ process input data:
--> Unmarshal(x, y) -> Objects(x) 
--> Reflection(x) -> Map(key, list<A>) 
--> DAO (MAP) -> ldap op(attrs)
+ process output data:
--> unload (entry) -> Map(key, list) 
--> Reflection (Map) -> Objects(x, y) 
--> Marshal (x, y) -> YAML files x,y

### Unmarshal
Takes the two YAML files as input and using Jackson converts into Java objects.

### Reflection
Uses Java reflection and converts the objects into a map of multi-values.

### DAO
Load the name/value pairs from the Map into LDAP attributes and creates the entry.

### Tests
Test cases in EntityTest.java under the test/java folder.

### Usage

Users must implement data model and entity classes. The model maps between the logical and physical data structure.

```
# sample LEM model:
key: uid
name: uid
full_name: cn
last_name: sn
password: userPassword
description: 
  - description
object_class: 
  - objectClass
```

This structure is compatible with inetorgperson in LDAP. It contains basic attributes like uid, cn, sn, description and userPassword.
LEM models require both key and object_class which are used to define a Relative Distinquished Name (RDN) for the entry along with the physical data structure in LDAP.

To define the data entity, all LDAP semantics and syntaxes are followed. To add an entry, the required fields must be present, otherwise an error will be returned from the API.

```
# add sample entity
key: acme1
name: acme1
full_name: acme one
last_name: one
password: secret
description: 
  - just a test
object_class:
  - inetorgperson
```

On the left are the model attribute names. The right contains the values.

For updates we need the key and whatever values to be replaced.

```
# update sample
key: acme1
description: 
  - another test
```

Deletes and reads have a key defined which contains the RDN of the node. Searches require a filter.

```
# delete/read/search sample
key: acme1
filter: *
```