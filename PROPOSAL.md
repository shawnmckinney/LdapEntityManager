# Directory User Manager Processes the YAML (DUMPTY)

## Overview

API uses YAML files to map between logical and physical data formats.


## Rationale

Work with apps like [SCIMPLE](https://directory.apache.org/scimple/) providing a flexible API layer to work with a variety of user data mapping req's. 

e.g.

### User Model

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

- The left side represents logical and right side is physical. Above we have mappings to both inetorgperson and posix account attrs.
- Lists are declared using YAML syntax rules. Single element lists declared in the model will map to multival attributes. Lists > 1 will be composed of mulitiple attrs.

### Sample User Entity

```
key: foo
# inetorgperson:  
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
#posixaccount:
id: 1234567
group_id: 7654321
home: /home/foo29
login: bash
type: rockstar    
```

### DUMPTY APIs

```
public interface EntityMgr 
{
    // Pass file names to the model and entity:
    void add( String modelFile, String dataFile, String className ) throws LemException;
    void update( String modelFile, String dataFile, String className ) throws LemException;
    void delete( String modelFile, String dataFile, String className ) throws LemException;
    Entity read( String modelFile, String dataFile, String className ) throws LemException;
    List<Entity> find( String modelFile, String dataFile, String className ) throws LemException;
    
    // Pass objects containing the model and entity:
    void add( Entity model, Entity data ) throws LemException;
    void update( Entity model, Entity data ) throws LemException;
    void delete( Entity model, Entity data ) throws LemException;
    Entity read( Entity model, Entity data ) throws LemException;
    List<Entity> find( Entity model, Entity data ) throws LemException;    
}
```
