# Directory User Manager Processes the YAML (DUMPTY)

## Overview

API uses YAML files to map between logical and physical data formats.


## Rationale

Provide a stable directory interface for apps like Apache [SCIMPLE](https://directory.apache.org/scimple/) that works with a variety of user data mapping req's. 

e.g.

### Sample User Model

```
key: uid
name: uid
full_name: cn
last_name: sn
first_name: givenname
password: userPassword
id: uidNumber
group_id: gidNumber
home: homeDirectory  
login: loginShell
type: employeeType    
description: 
  - description
object_class: 
  - objectClass
addresses:
  - postalAddress
  - l
  - postalCode  
phones:
  - telephonenumber
emails:
  - mail
```

- The left side is entity's logical attr name and the right side is the physical attr name.
- Lists are declared using YAML syntax rules. 
- Single element lists in the model map to multival attrs. 
- Lists with more than one element are composed of multiple attrs.

### Sample User Entity

```
key: foo
name: foo
full_name: foo fighters
last_name: fighters
first_name: foo
password: ****
description: 
  - Whatever
  - It
  - Takes      
object_class:
  - inetorgperson
  - posixaccount
addresses:
  - Suite 2112$157 Riverside Ave
  - Pasedena
  - 77777
phones: 
  - 555-333-3333
  - 867-5309
emails:
  - foo29@fighters.com
  - foo.fighters@gmail.com
id: 1234567
group_id: 7654321
home: /home/foo29
login: bash
type: rockstar    
```

- The left side contains the logical attr name and the right side is the attr value.
- In this example, the object_class, phones and emails attrs may be loaded with multiple values.
- Lists of multiple attrs must follow mapping rule.
e.g. 
Model attrs: 
```
addresses:
  - postalAddress
  - l
  - postalCode
```

Data attr values in the same order as the model:
```
addresses:
  - Suite 2112$157 Riverside Ave
  - Pasedena
  - 77777
```

### Sample User Objects

- Users define the logical model with a Java entity class (e.g. src/test/org/apache/directory/lem/User.java).
- Users define the logical-to-physical mapping with YAML (e.g. /src/test/conf/users.yml).
- User may input data either with YAML files or Java classes (e.g. src/test/org/apache/directory/lem/EntityTest.java).
- LDAP bindings are contained in file (src/test/conf/config.properties).

### DUMPTY APIs

```
public interface EntityMgr 
{
    // Pass file names to the model and entity:
    void add( String modelFile, String dataFile, String className )
    void update( String modelFile, String dataFile, String className )
    void delete( String modelFile, String dataFile, String className )
    Entity read( String modelFile, String dataFile, String className )
    List<Entity> find( String modelFile, String dataFile, String className )
    
    // Pass objects containing the model and entity:
    void add( Entity model, Entity data )
    void update( Entity model, Entity data )
    void delete( Entity model, Entity data )
    Entity read( Entity model, Entity data )
    List<Entity> find( Entity model, Entity data )
}
```
