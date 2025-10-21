# Ansible Integration for LDAP Entity Manager

This directory contains Ansible playbooks and roles for managing LDAP entities using the LDAP Entity Manager.

## Quick Start

### Prerequisites

- Ansible >= 2.9
- LDAP server (OpenLDAP, Active Directory, etc.)
- Java 11+ and Maven 3+ (for building the LEM jar)
- LDAP Entity Manager installed and configured

### Building the LEM Package

First, build the LDAP Entity Manager:

```bash
cd ..
mvn clean install
```

### Running the Example Playbooks

1. Update the inventory file with your target hosts:
   ```bash
   vi inventory/hosts
   ```

2. Configure your LDAP connection details in the role defaults:
   ```bash
   vi roles/openldap/defaults/main.yml
   ```

3. Run the playbook:
   ```bash
   ansible-playbook -i inventory/hosts site.yml
   ```

## Common Issues and Solutions

### Loop Error: "Invalid data passed to 'loop', it requires a list, got this instead: 1,2,3,4..."

**Problem**: This error occurs when Ansible receives a comma-separated string instead of a proper list for the `loop` directive.

**Common Causes**:

1. **Using `lookup('sequence')` without `wantlist=True`**:
   ```yaml
   # ❌ WRONG - Returns a string
   loop: "{{ lookup('sequence', 'start=1 end=100') }}"
   
   # ✅ CORRECT - Returns a list
   loop: "{{ lookup('sequence', 'start=1 end=100', wantlist=True) }}"
   ```

2. **Using `range()` without converting to list properly**:
   ```yaml
   # ❌ WRONG - May not work in all Ansible versions
   loop: "{{ range(1, 101) }}"
   
   # ✅ CORRECT - Explicitly convert to list
   loop: "{{ range(1, 101) | list }}"
   ```

3. **Using `query()` vs `lookup()`**:
   ```yaml
   # ✅ Use query() for lists (recommended)
   loop: "{{ query('sequence', 'start=1 end=100') }}"
   
   # ✅ Or use lookup with wantlist=True
   loop: "{{ lookup('sequence', 'start=1 end=100', wantlist=True) }}"
   ```

### Example: Adding 100 Test Users

See the example playbook `add-test-users.yml` which demonstrates the correct way to loop over a sequence of users.

## Directory Structure

```
ansible/
├── README.md                          # This file
├── site.yml                           # Main playbook
├── add-test-users.yml                 # Example: Add test users
├── inventory/
│   └── hosts                          # Inventory file
└── roles/
    └── openldap/
        ├── defaults/
        │   └── main.yml               # Default variables
        ├── tasks/
        │   └── main.yml               # Task definitions
        └── templates/
            ├── user.yml.j2            # User entity template
            └── user-model.yml.j2      # User model template
```

## Playbook Examples

### Adding Users to Test Zones

The `add-test-users.yml` playbook shows how to properly add multiple users using a loop:

```yaml
- name: Add users to test zones
  include_role:
    name: openldap
    tasks_from: add-user
  loop: "{{ range(1, 101) | list }}"  # Correct way to loop from 1 to 100
  loop_control:
    loop_var: user_id
```

### Using Sequence Lookup

Alternatively, you can use the `sequence` lookup:

```yaml
- name: Add users to test zones
  include_role:
    name: openldap
    tasks_from: add-user
  loop: "{{ query('sequence', 'start=1 end=100') }}"  # Using query()
  loop_control:
    loop_var: user_id
```

## Configuration

### LDAP Connection

Configure your LDAP connection in `roles/openldap/defaults/main.yml`:

```yaml
ldap_host: localhost
ldap_port: 389
ldap_bind_dn: "dc=example,dc=com"
ldap_bind_pw: secret
ldap_base_dn: "ou=people,dc=example,dc=com"
```

### User Attributes

Customize user attributes by modifying the templates in `roles/openldap/templates/`.

## Best Practices

1. **Always use `wantlist=True` with lookup()** when you need a list
2. **Use `query()` instead of `lookup()`** when working with lists (cleaner syntax)
3. **Convert `range()` output to list** using the `list` filter
4. **Test with a small subset** before running on large ranges (e.g., test with 1-10 before 1-100)
5. **Use `loop_control`** to customize loop variable names for better readability

## Additional Resources

- [Ansible Loops Documentation](https://docs.ansible.com/ansible/latest/user_guide/playbooks_loops.html)
- [Ansible Lookup Plugins](https://docs.ansible.com/ansible/latest/plugins/lookup.html)
- [LDAP Entity Manager Documentation](../README.md)
