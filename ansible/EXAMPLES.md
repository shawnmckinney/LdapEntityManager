# Ansible Examples for LDAP Entity Manager

This document provides practical examples for using Ansible with LDAP Entity Manager.

## Table of Contents

1. [Basic Setup](#basic-setup)
2. [Adding Test Users](#adding-test-users)
3. [Custom User Ranges](#custom-user-ranges)
4. [Production Use Cases](#production-use-cases)
5. [Error Handling](#error-handling)

## Basic Setup

### 1. Configure Your LDAP Connection

Edit `roles/openldap/defaults/main.yml`:

```yaml
ldap_host: your-ldap-server.example.com
ldap_port: 389
ldap_bind_dn: "cn=admin,dc=example,dc=com"
ldap_bind_pw: "your-secure-password"
ldap_people_base: "ou=users,dc=example,dc=com"
```

### 2. Update Inventory

Edit `inventory/hosts`:

```ini
[ldap_servers]
ldap1.example.com

[ldap_servers:vars]
ansible_user=ansible
ansible_become=yes
```

## Adding Test Users

### Example 1: Add 10 Test Users

```bash
# Using the built-in playbook
ansible-playbook add-test-users.yml -e "test_count=10"
```

This creates users: testuser1, testuser2, ..., testuser10

### Example 2: Add 100 Test Users (as in the problem statement)

```bash
# Full range (1-100)
ansible-playbook add-test-users.yml
```

### Example 3: Custom User Prefix

```bash
# Create users: employee1, employee2, etc.
ansible-playbook add-test-users.yml \
  -e "test_user_prefix=employee" \
  -e "test_count=50"
```

## Custom User Ranges

### Example 4: Non-Sequential User IDs

Create a custom playbook `add-specific-users.yml`:

```yaml
---
- name: Add specific users
  hosts: localhost
  connection: local
  gather_facts: no

  vars:
    specific_user_ids: [5, 10, 15, 20, 25]  # Only these user IDs

  tasks:
    - name: Add users with specific IDs
      include_role:
        name: openldap
        tasks_from: add-user
      loop: "{{ specific_user_ids }}"
      loop_control:
        loop_var: user_id
```

Run it:
```bash
ansible-playbook add-specific-users.yml
```

### Example 5: Range with Step

Add every 5th user from 1 to 100:

```yaml
---
- name: Add users with step
  hosts: localhost
  connection: local
  gather_facts: no

  tasks:
    - name: Add users (1, 6, 11, 16, ...)
      include_role:
        name: openldap
        tasks_from: add-user
      loop: "{{ range(1, 101, 5) | list }}"  # Step of 5
      loop_control:
        loop_var: user_id
```

### Example 6: Multiple Ranges

Add users from different ID ranges:

```yaml
---
- name: Add users from multiple ranges
  hosts: localhost
  connection: local
  gather_facts: no

  vars:
    user_ranges:
      - "{{ range(1, 11) | list }}"      # 1-10
      - "{{ range(100, 111) | list }}"   # 100-110
      - "{{ range(500, 511) | list }}"   # 500-510

  tasks:
    - name: Add users from each range
      include_role:
        name: openldap
        tasks_from: add-user
      loop: "{{ user_ranges | flatten }}"
      loop_control:
        loop_var: user_id
```

## Production Use Cases

### Example 7: Add Users from a CSV File

Create `users.csv`:
```csv
username,uid,gid,home,shell
jdoe,10001,10001,/home/jdoe,/bin/bash
asmith,10002,10002,/home/asmith,/bin/bash
```

Create playbook `add-from-csv.yml`:

```yaml
---
- name: Add users from CSV
  hosts: localhost
  connection: local
  gather_facts: no

  tasks:
    - name: Read users from CSV
      read_csv:
        path: users.csv
      register: user_data

    - name: Add each user to LDAP
      include_role:
        name: openldap
        tasks_from: add-user
      loop: "{{ user_data.list }}"
      loop_control:
        loop_var: user_data_item
      vars:
        current_user_name: "{{ user_data_item.username }}"
        current_user_uid: "{{ user_data_item.uid }}"
        current_user_gid: "{{ user_data_item.gid }}"
        current_user_home: "{{ user_data_item.home }}"
        test_user_shell: "{{ user_data_item.shell }}"
```

### Example 8: Add Users with Different Attributes

Create `add-custom-users.yml`:

```yaml
---
- name: Add users with custom attributes
  hosts: localhost
  connection: local
  gather_facts: no

  vars:
    custom_users:
      - name: john.doe
        uid: 20001
        gid: 20001
        shell: /bin/bash
        type: developer
      - name: jane.smith
        uid: 20002
        gid: 20002
        shell: /bin/zsh
        type: manager

  tasks:
    - name: Add each custom user
      include_role:
        name: openldap
        tasks_from: add-user
      loop: "{{ custom_users }}"
      loop_control:
        loop_var: custom_user
      vars:
        current_user_name: "{{ custom_user.name }}"
        current_user_uid: "{{ custom_user.uid }}"
        current_user_gid: "{{ custom_user.gid }}"
        test_user_shell: "{{ custom_user.shell }}"
```

### Example 9: Batch Processing with Error Handling

```yaml
---
- name: Add users with error handling
  hosts: localhost
  connection: local
  gather_facts: no

  tasks:
    - name: Add users to test zones
      block:
        - name: Add each user
          include_role:
            name: openldap
            tasks_from: add-user
          loop: "{{ range(1, 101) | list }}"
          loop_control:
            loop_var: user_id
          register: add_results
          
      rescue:
        - name: Log error
          debug:
            msg: "Error adding users: {{ ansible_failed_result }}"
        
        - name: Continue with cleanup or notification
          # Your cleanup tasks here
          debug:
            msg: "Cleanup task placeholder"
      
      always:
        - name: Report completion status
          debug:
            msg: "User addition process completed"
```

## Error Handling

### Example 10: Handle Existing Users

Create `add-users-idempotent.yml`:

```yaml
---
- name: Add users (skip if exists)
  hosts: localhost
  connection: local
  gather_facts: no

  tasks:
    - name: Add users to test zones
      include_role:
        name: openldap
        tasks_from: add-user
      loop: "{{ range(1, 11) | list }}"
      loop_control:
        loop_var: user_id
      register: add_result
      failed_when: false  # Don't fail if user already exists

    - name: Report results
      debug:
        msg: "{{ add_result.results | selectattr('failed', 'equalto', false) | list | length }} users added successfully"
```

### Example 11: Dry Run Mode

Test your configuration without making changes:

```yaml
---
- name: Dry run - Simulate user addition
  hosts: localhost
  connection: local
  gather_facts: no

  vars:
    dry_run: yes

  tasks:
    - name: Show what would be created
      debug:
        msg: "Would create user testuser{{ item }} with UID {{ 10000 + item }}"
      loop: "{{ range(1, 11) | list }}"
      when: dry_run | default(false)

    - name: Actually create users
      include_role:
        name: openldap
        tasks_from: add-user
      loop: "{{ range(1, 11) | list }}"
      loop_control:
        loop_var: user_id
      when: not (dry_run | default(false))
```

Run:
```bash
# Dry run (no changes)
ansible-playbook add-users-idempotent.yml -e "dry_run=yes"

# Actual run
ansible-playbook add-users-idempotent.yml -e "dry_run=no"
```

### Example 12: Throttling (Avoid Overwhelming LDAP Server)

Add users slowly to avoid overloading the LDAP server:

```yaml
---
- name: Add users with throttling
  hosts: localhost
  connection: local
  gather_facts: no

  tasks:
    - name: Add users to test zones (throttled)
      include_role:
        name: openldap
        tasks_from: add-user
      loop: "{{ range(1, 101) | list }}"
      loop_control:
        loop_var: user_id
        pause: 0.5  # Wait 0.5 seconds between each user
      throttle: 5   # Maximum 5 concurrent operations
```

## Performance Tips

### Example 13: Parallel Execution

For large batches, use parallel execution:

```yaml
---
- name: Add users in parallel batches
  hosts: localhost
  connection: local
  gather_facts: no
  strategy: free  # Allow parallel execution

  tasks:
    - name: Add users in batches of 10
      include_role:
        name: openldap
        tasks_from: add-user
      loop: "{{ range(1, 101) | batch(10) | list }}"
      loop_control:
        loop_var: user_batch
```

## Testing Your Configuration

Before running against production:

1. **Run the quick test**:
   ```bash
   ansible-playbook quick-test.yml
   ```

2. **Test with a small range**:
   ```bash
   ansible-playbook add-test-users.yml -e "test_count=3"
   ```

3. **Use check mode**:
   ```bash
   ansible-playbook add-test-users.yml --check
   ```

4. **Enable verbose output**:
   ```bash
   ansible-playbook add-test-users.yml -vvv
   ```

## Common Patterns Summary

```yaml
# Pattern 1: Simple sequential range
loop: "{{ range(1, 101) | list }}"

# Pattern 2: Query with sequence (Ansible native)
loop: "{{ query('sequence', 'start=1 end=100') }}"

# Pattern 3: Lookup with wantlist
loop: "{{ lookup('sequence', 'start=1 end=100', wantlist=True) }}"

# Pattern 4: Range with step
loop: "{{ range(0, 100, 5) | list }}"  # 0, 5, 10, 15, ...

# Pattern 5: Specific list
loop: [1, 5, 10, 50, 100]

# Pattern 6: From variable
loop: "{{ my_user_ids }}"

# Pattern 7: From file
loop: "{{ lookup('file', 'user_ids.txt').split('\n') }}"

# Pattern 8: Multiple ranges combined
loop: "{{ (range(1, 11) | list) + (range(100, 111) | list) }}"
```

## Additional Resources

- Main README: [ansible/README.md](README.md)
- Troubleshooting Guide: [ansible/TROUBLESHOOTING.md](TROUBLESHOOTING.md)
- Error Demonstration: [ansible/error-demonstration.yml](error-demonstration.yml)
- Quick Test: [ansible/quick-test.yml](quick-test.yml)
