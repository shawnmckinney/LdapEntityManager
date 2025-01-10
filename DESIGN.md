# LDAP Entity Manager (LEM) Design

## Overview

Use LDAP protocol to Create Read Update Delete (CRUD) and search records in the directory database.
External data interface uses Yet Another Markup Language (YAML) files and/or Java objects to process arbitrary data models.
Multi-value maps for internal processing abstracts the details of the data model from the data access layer.

## Data Model and API
    - [see](./PROPOSAL.md)

## External Files, Dependencies and Runtime

Use two input files. The first, model, defines the mappings between the logical and physical format. 
The second, entity, contains the actual data in logical format defined by the model.

Run on the Java platform and use ...
- Apache Directory LDAP API for data access.
- Apache Commons Configuration for property storage.
- Apache Log4j2 for logging utility.
- Jackson for data binding.

Sample data model, entity files and configs are under the test/conf project folder.

## API Design

Defined in the EntityMgr interface. Two options for usage:

1. Accept YAML files as args, unmarshal before calling #2 (automatically).
    - do( String modelFile, String dataFile, String className )
    - fully qualified className of the entity.
2. Pass Java objects as args
    - do( Entity model, Entity data )

- Both are functionally equivalent.

### Iterface Description

1. Create, Update, Delete methods:
+ Input: 2 YAML files; model x, entity y 
+ process flow:
--> Unmarshal YAML -> Java Objects
--> Java Reflection -> Map
--> DAO (Map) -> load LDAP (entry)

2. Read, Search methods:
+ Input: 1 YAML files; model x, 1 key 
+ process input data:
--> Unmarshal YAML -> Java Objects 
--> Reflection Java Objects -> Map
--> DAO (Map) -> search LDAP (dn, filter)
+ process output data:
--> unload LDAP (entries) -> Map
--> Java Reflection Map -> Java Objects
--> Marshal Java Objects -> YAML

### Unmarshal YAML
Accept two YAML files as input and convert into Java objects with Jackson.

### Java Multi-Valued Map
Convert Java objects into multi-valued maps with Java reflection.

### DAO Module Description
These classes handle processing between the interface and backend database resource.

| Java Classname     | has the following function                   |
|--------------------|----------------------------------------------|
| EntityDao          | Create, Read, Update, Delete, Search LDAP    |
| BaseDao            | Apache LDAP API wrapper                      |
| ConnectionProvider | LDAP connection pool processing              |
| ResourceUtil       | Perform Jackson Data Binding ops             |
| TrustStoreManager  | Used in LDAPS/TLS connections                |

- EntityDao passes data using a multi-valued map.
- Support various data formats without modification to the DAO module code.

### Entity Mapper Description

- Uses Apache Commons Collections to store LDAP attributes as name, value pairs inside a map. Values may be single or multivalued.
- Key map by the LDAP attr name. Store attribute value in an ArrayList of Strings. 
- Do not expose map outside of DAO module.

Sample LEM record:

```
# Objects at runtime in debugger (after call to unloadMap).
# 1. Logical: org.apache.directory.lem.Group
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
    [0] = (HashMap$Node)
        key = (String) "member"	
        value = (ArrayList) "size = 4"
        [0] = (String) "uid=foo26,ou=People,dc=example,dc=com"	
        [1] = (String) "uid=foo27,ou=People,dc=example,dc=com"	
        [2] = (String) "uid=foo28,ou=People,dc=example,dc=com"	
        [3] = (String) "uid=foo29,ou=People,dc=example,dc=com"	
    [1] = (HashMap$Node)
        key = (String) "objectClass"
        value = (ArrayList) "size = 2"
            [0] = (String) "groupofmembers"
            [1] = (String) "posixgroup"
    [2] = (HashMap$Node)
        key = (String) "description"	
        value = (ArrayList) "size = 1"	
            [0] = (String) "foo"	
    [3] = (HashMap$Node)
        key = (String) "dn"	
        value = (ArrayList) "size = 1"
            [0] = (String) "cn=foo3,ou=groups,dc=example,dc=com"		
    [4] = (HashMap$Node)
        key = (String) "cn"	
        value = (ArrayList) "size = 1"	
            [0] = (String) "foo3"	
    [5] = (HashMap$Node)
        key = (String) "gidNumber"	
        value = (ArrayList) "size = 1"	
            [0] = (String) "987654321"
```

### Tests
Test cases in EntityTest.java under the test/java folder.

### Usage
(Work In Progress)

#### Java Class

Jackson requires a Java class for target of data binding operations.

The Java class ...
- contains the target data mappings where the logical and physical element names are stored.
- an attribute named 'key'.
- an attribute named 'object_class'.
- implements the org.apache.directory.lem.Enitity and java.io.Serializable interfaces.
- contains other fields as required by the User.
- fields are defined as type String or List for single or multivalued attrs. 

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

A model file is created to map between the logical and physical attribute names.

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
- Requires a field named 'key' which contains the physical attribute name of the Relative Distinquished Name (RDN) of the LDAP node.
- Requires a field named 'object_class' which contains the LDAP attribute name for objectClass.
- All LDAP structural and auxilliary objectClasses are supported.

#### YAML Entity File

Required when using interface type #1. It contains the actual data to be processed by the APIs.
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

Deletes and reads have a 'key' to contain node's RDN. Searches require a 'filter'.

```
# delete/read/search sample
key: acme1
filter: (uid=foo*)
```

#### Entity Declarations

In the config.properties file are declarations that have the fully qualified class name on the left and its LDAP base DN (address) on the right.

```
# config.properties
...
# Users in ou=People, ...
org.apache.directory.lem.User=ou=people,dc=example,dc=com
# Groups in ou=Groups, ...
org.apache.directory.lem.Group=ou=groups,dc=example,dc=com
```