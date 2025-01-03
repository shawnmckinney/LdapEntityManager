# Overview

LDAP Entity Manager (LEM) reads and writes from LDAP. It uses its protocol to Create Read Update Delete (CRUD) and search.
The mappings between the backend database and the entities are defined in Yet Another Markup Language (YAML) format and Java classes.

## YAML model, entitity data and API
    - [see](./PROPOSAL.md)

## Details

There are two input files. The first, entity model, defines the mappings between the logical and physical format. 
The second, entity data, contains the actual data using the logical format defined in the model.

LEM Uses Java and
- Apache Directory LDAP API for user data access.
- Apache Commons Configuration for user property storage.
- Apache Log4j2 for logging utility.
- Sample data model, entity files and config under test/conf.

## Design

LEM APIs are defined in the EntityMgr interface. There are two usage options:

1. Passing YAML files as args
    - add( String modelFile, String dataFile, String className )
    - className is name of Java class name of the model and entity.
2. Passing Java objects as args
    - void add( Entity model, Entity data )

- #1 unmarshals the YAML into Java objects before calling 2 (automatically). 
- #2 accepts Java objects as arguments.
- Both are functionally equivilent.

## Operations Supported

1. Create, Update or Delete
+ Input: 2 YAML files; model x, entity y 
+ process flow:
--> Unmarshal YAML -> Java Objects
--> Java Reflection -> Map
--> DAO (Map) -> load LDAP (entry)

2. Read or Search 
+ Input: 1 YAML files; model x, 1 key 
+ process input data:
--> Unmarshal YAML -> Java Objects 
--> Reflection Java Objects -> Map
--> DAO (Map) -> search LDAP (dn, filter)
+ process output data:
--> unload LDAP (entries) -> Map
--> Java Reflection Map -> Java Objects
--> Marshal Java Objects -> YAML

### Unmarshal
Takes the two YAML files as input and using Jackson converts into Java objects.

### Java Reflection
Converts the objects into a map of multi-values.

### DAO Module

These classes handle processing between the interface and backend database resource.

| Java Classname     | has the following function                   |
|--------------------|----------------------------------------------|
| BaseDao            | Apache LDAP API wrapper                      |
| ConnectionProvider | LDAP connection pool processing              |
| ResourceUtil       | Perform Jackson Data Binding ops             |
| TrustStoreManager  | Used in LDAPS/TLS connections                |
| EntityDao          | Create, Read, Update, Delete, Search LDAP    |

The EntityDao design prescribes passing data objects in and out as a Multimap.
The map contains the list of attribute names and their associated values.
Because of this, its interface should not change.
 
This is the advantage of LEM. Adapting to a variety of complex mappings without modifying the DAO module code.

### Entity Mapper
Uses Apache Commons Collections for storing LDAP attributes as name, value pairs inside a map. The values may be single or multivalued.
The map is keyed by the LDAP attr name and its value is an ArrayList of Strings. The LDAP API will set the proper attribute types in the database per schema requirements.
The mapper is used internally for entity conversions from logical to physical names as required by the DAO module APIs.

Sample LEM record:

```
# Objects at runtime in debugger (after call to unloadMap).
# 1. Logical: org.apache.directory.lem.Group.java
    data = (Group)	
    name = (String) "foo3"	
    description = (String) "foo"	
    key = (String) "foo3"	
    id = (String) "987654321"	
    members = (ArrayList) "size = 4"	
        [0] = (String) "uid=foo26,ou=People,dc=example,dc=com"	
        [1] = (String) "uid=foo27,ou=People,dc=example,dc=com"	
        [2] = (String) "uid=foo28,ou=People,dc=example,dc=com"	
        [3] = (String) "uid=foo29,ou=People,dc=example,dc=com"	
    object_class = (ArrayList) "size = 2"	
        [0] = (String) "groupofmembers"	
        [1] = (String) "posixgroup"	
    
# 2. Physical: org.apache.commons.collections4.multimap.ArrayListValuedHashMap
    map = (HashMap) size = 6
    [0] = (HashMap$Node) "member => [uid=foo26,ou=People,dc=example,dc=com, uid=foo27,ou=People,dc=example,dc=com, uid=foo28,ou=People,dc=example,dc=com, uid=foo29,ou=People,dc=example,dc=com]"
        key = (String) "member"	
        value = (ArrayList) "size = 4"
        [0] = (String) "uid=foo26,ou=People,dc=example,dc=com"	
        [1] = (String) "uid=foo27,ou=People,dc=example,dc=com"	
        [2] = (String) "uid=foo28,ou=People,dc=example,dc=com"	
        [3] = (String) "uid=foo29,ou=People,dc=example,dc=com"	
    [1] = (HashMap$Node) "objectClass => [groupofmembers, posixgroup]"
        key = (String) "objectClass"
        value = (ArrayList) "size = 2"
            [0] = (String) "groupofmembers"
            [1] = (String) "posixgroup"
    [2] = (HashMap$Node) "description => [foo]"	
        key = (String) "description"	
        value = (ArrayList) "size = 1"	
    [3] = (HashMap$Node) "dn => [cn=foo3,ou=groups,dc=example,dc=com]"	
        key = (String) "dn"	
        value = (ArrayList) "size = 1"
            [0] = (String) "cn=foo3,ou=groups,dc=example,dc=com"		
    [4] = (HashMap$Node) "cn => [foo3]"	
        key = (String) "cn"	
        value = (ArrayList) "size = 1"	
            [0] = (String) "foo3"	
    [5] = (HashMap$Node) "gidNumber => [987654321]"	
        key = (String) "gidNumber"	
        value = (ArrayList) "size = 1"	
            [0] = (String) "987654321"
```


### Tests
Test cases in EntityTest.java under the test/java folder.

### Usage
(Work In Progress)

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