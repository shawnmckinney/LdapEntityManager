# Overview

LDAP Entity Manager (LEM) reads and writes records from an LDAP database. It uses LDAPv3 protocol to Create Read Update Delete (CRUD).
The mappings between the backend database and the entities are defined in Yet Another Markup Language (YAML) format and Java classes.
There are two input files to the APIs. The first, the entity's model, defines the mappings between the logical and physical format. 
The second, the entity's data, contains the actual data using the logical format defined in the model.

## YAML model, entitity data and API
    - [see](./PROPOSAL.md)

## Details
- LEM uses Apache Directory LDAP API for data access. 
- Apache Commons Configuration is used for LDAP coordinates and stored in the config.properties file as name:value pairs.
- Apache Log4j2 for logging and its config in log4j2.xml.
- The data model and entity files are in test/conf folder.

## Design

LEM APIs are defined in the EntityMgr.java interface and have two usage options:

1. Passing YAML files as args
    - add( String modelFile, String dataFile, String className )
    - className is name of Java class name of the model and entity.
2. Passing Java objects as args
    - void add( Entity model, Entity data )

- #1 unmarshals the YAML into Java objects before calling 2 (automatically). 
- #2 accepts Java objects as arguments.
- Both are functionally equivilent.

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

### Java Reflection
Converts the objects into a map of multi-values.

### DAO
Load the name/value pairs from the Map into LDAP attributes and perform the LDAP operation.

Contained in the dao package. It's comprised of the following:
- BaseDao.java: boilerplate functionality for interacting with the LDAP data layer.
- ConnectionProvider.java: a connection pool implementation.
- ResourceUtil.java utilities for processing needed by the DAO functions.
- TrustStoreManager.java used during LDAPS and TLS connections.
- EntityDao.java functions to read, write and search the LDAP entities.

Of these, the EntityDao's design is novel. It depends on the caller passing the input data
in maps containing a dictionary of attribute names and their associated values. 
It's expected that this code will have to change as it matures to handle more complex use cases. 
Eventually, if the design's successful, it'll stabilize.

This is the advantage of LEM over other LDAP CRUD APIs. Adapting to a variety of complex mappings without code changes.

### Tests
Test cases in EntityTest.java under the test/java folder.

### Usage

Some guidelines for usage follow (Work In Progress)

#### Java Class

- A Java class to contain the target data structure where the mappings between the logical and physical element names are stored.
- An attribute named key is required.
- An attribute named object_class is required.
- The class must implement the org.apache.directory.lem.Enitity and java.io.Serializable interfaces.
- All other field names and types are defined by the User per their target LDAP system reqs.
- The data types correspond with the physical model. (Directory) Strings, Integers, Booleans and other LDAP schema attribute types are (will be) supported.

```
public class UserSample implements Entity, Serializable
{
    private String key;                // required
    private List<String> object_class; // required
    private String name;
    private String full_name;
    private String last_name;
    private String password;    
    private List<String> description;
# getters and setters follow:
...
```

#### YAML Model File

A model file is created to map between the logical and physical data structure. Its attribute names correspond with the entity class.

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

- This sample structure is compatible with inetorgperson in LDAP. It contains attributes like uid, cn, sn, description and userPassword.
- LEM requires a field named 'key' which contains the physical attribute name of the Relative Distinquished Name (RDN) of the LDAP node.
- LEM requires a field named 'object_class' which contains the LDAP attribute name for objectClass.
- All LDAP strutural and auxilliary objectClasses are supported. All LDAP attributes are supported.


#### YAML Entity File

A data entity file is required when using interface type #1. It contains the actual data to be processed by the APIs.
LDAP semantics and syntaxes must be followed. Errors in LDAP are returned as checked Exceptions from the APIs.


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

#### Entity Declarations

All LEM entities must be declared in its config.properties file. 
These contain the fully qualified class name of the entity on the left side and its LDAP coordinates on the right.

```
# config.properties
...
org.apache.directory.lem.User=ou=people,dc=example,dc=com
org.apache.directory.lem.Group=ou=groups,dc=example,dc=com
```