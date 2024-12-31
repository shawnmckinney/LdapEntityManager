# Overview

Ldap Entry Manager (LEM) reads and writes records from an LDAP database. It uses LDAPv3 protocol for Create Read Update Delete (CRUD).
The mapping between the backend database and the entity that is exposed via the API is defined in Yet Another Markup Language (YAML) format.
There are two input files to the APIs. The first is the entity model which defines the mapping between the logical and physical format. 
The second contains the actual data using the logical definitions for referencing the physical data values.

## Details
- LEM uses Apache Directory LDAP API for data access. The configuration for target LDAP instance is stored in the config.properties file as name:value pairs.
- Apache Log4j2 is the used for logging. It uses a configuration file name log4j2.xml.
- The data input (model and entity) are stored in the test/conf folder.

e.g.
Create entry:

#YAML model

#YAML entity data

#API

## Design

LEM APIs are defined in the EntityMgr.java interface. There are two ways of interacting with it:

1. Passing YAML files
2. Passing Java objects

- It's more efficient to use #2 as it saves the step of marshaling the YAML into Java objects. 

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