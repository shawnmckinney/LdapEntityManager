# Directory User Manager Processes the YAML (DUMPTY)

## Overview

API uses YAML files to map between logical and physical data formats.


## Rationale

Work with apps like [SCIMPLE](https://directory.apache.org/scimple/) providing a stable API layer to work with a variety of user data mapping req's. 

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

- The left side represents logical and right side is physical names of the attributes.
- Lists are declared with YAML syntax rules. Single element lists declared in the model will map to multival attrs. Lists > 1 will be composed of multiple attrs.

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

- The left side contains the logical attr name and the right side has the value.
- The object_class attr may be loaded with multiple values.
- Lists composed of multiple attrs are a special case and must follow mapping rule.
e.g. model: 
```
addresses:
  - postalAddress
  - l
  - postalCode
```

Data values must be listed in the same order:
```
addresses:
  - Suite 2112$157 Riverside Ave
  - Pasedena
  - 77777
```

### Sample User Objects

- Users will implement their data model by defining entity classes (samples under src/test/org/apache/directory/lem) and the corresponding YAML model and data artifacts (e.g. /src/test/conf)
- LDAP bindings are contained in (src/test/conf) config.properties file.

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
