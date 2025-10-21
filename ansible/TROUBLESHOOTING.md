# Ansible Loop Troubleshooting Guide

## The "Invalid data passed to 'loop'" Error

### Problem Description

You encounter this error when running Ansible playbooks:

```
TASK [openldap : add users to test zones] ************************************************
fatal: [tsapma]: FAILED! => {
  "msg": "Invalid data passed to 'loop', it requires a list, got this instead: 
  1,2,3,4,5,6,7,8,9,10,...,98,99,100. 
  Hint: If you passed a list/dict of just one element, try adding wantlist=True 
  to your lookup invocation or use q/query instead of lookup."
}
```

### Root Cause

The `loop` directive in Ansible expects a **list** (array) but is receiving a **string** containing comma-separated values. This typically happens when using lookup plugins or functions that return strings by default.

### Solutions

#### Solution 1: Use `range()` with `list` filter (RECOMMENDED)

This is the most straightforward and Pythonic approach:

```yaml
- name: Add users to test zones
  include_role:
    name: openldap
    tasks_from: add-user
  loop: "{{ range(1, 101) | list }}"  # ✅ Correct
  loop_control:
    loop_var: user_id
```

**Why it works**: The `range()` function returns a range object, and the `| list` filter explicitly converts it to a list.

#### Solution 2: Use `query()` instead of `lookup()`

The `query()` function always returns a list:

```yaml
- name: Add users to test zones
  include_role:
    name: openldap
    tasks_from: add-user
  loop: "{{ query('sequence', 'start=1 end=100') }}"  # ✅ Correct
  loop_control:
    loop_var: user_id
```

**Why it works**: `query()` is specifically designed to return lists and is the recommended way to work with loops in modern Ansible.

#### Solution 3: Add `wantlist=True` to `lookup()`

If you must use `lookup()`, add the `wantlist=True` parameter:

```yaml
- name: Add users to test zones
  include_role:
    name: openldap
    tasks_from: add-user
  loop: "{{ lookup('sequence', 'start=1 end=100', wantlist=True) }}"  # ✅ Correct
  loop_control:
    loop_var: user_id
```

**Why it works**: The `wantlist=True` parameter tells `lookup()` to return a list instead of a comma-separated string.

### Common Mistakes

#### Mistake 1: Using `lookup()` without `wantlist=True`

```yaml
# ❌ WRONG - Returns a string!
loop: "{{ lookup('sequence', 'start=1 end=100') }}"
```

**Error**: This returns `"1,2,3,4,5,...,100"` as a string.

#### Mistake 2: Using `range()` without `list` filter

```yaml
# ❌ WRONG - May not work consistently
loop: "{{ range(1, 101) }}"
```

**Error**: Depending on the Ansible version, this may not be properly converted to a list.

#### Mistake 3: Treating the result as already being a list

```yaml
# ❌ WRONG - The result is still a string
loop: "{{ lookup('sequence', 'start=1 end=100').split(',') }}"
```

**Error**: While this might seem to work, you're manually parsing what should be handled by Ansible.

### Understanding the Difference: `lookup()` vs `query()`

| Feature | `lookup()` | `query()` |
|---------|------------|-----------|
| Default return type | String (comma-separated) | List |
| Requires `wantlist` | Yes, for lists | No |
| Recommended for loops | No | Yes |
| Ansible version | All versions | 2.5+ |

### Testing Your Loop

Before running your playbook on 100 items, test with a small range:

```yaml
# Test with just 3 items first
loop: "{{ range(1, 4) | list }}"  # Will loop over [1, 2, 3]
```

Or use a variable:

```yaml
# Define at playbook level or pass with -e
vars:
  test_count: 10

tasks:
  - name: Add users
    include_role:
      name: openldap
      tasks_from: add-user
    loop: "{{ range(1, test_count | int + 1) | list }}"
    loop_control:
      loop_var: user_id
```

Then run:
```bash
# Test with 10 users
ansible-playbook add-test-users.yml -e "test_count=10"

# Run with full 100 users once tested
ansible-playbook add-test-users.yml -e "test_count=100"
```

### Debugging Loop Issues

#### Check what type of data you're getting:

```yaml
- name: Debug loop variable type
  debug:
    msg:
      - "Type: {{ item | type_debug }}"
      - "Value: {{ item }}"
  loop: "{{ range(1, 4) | list }}"
```

#### Verify the loop is working before including the role:

```yaml
- name: Test loop only
  debug:
    msg: "Processing item {{ item }}"
  loop: "{{ range(1, 4) | list }}"
```

### Advanced: Custom Number Ranges

#### Non-sequential ranges (with step):

```yaml
# Every 5th number from 1 to 100
loop: "{{ range(1, 101, 5) | list }}"  # [1, 6, 11, 16, ..., 96]
```

#### Using sequence with formatting:

```yaml
# Zero-padded numbers: 001, 002, 003, etc.
loop: "{{ query('sequence', 'start=1 end=100 format=testuser%03d') }}"
```

#### From a variable:

```yaml
vars:
  user_ids: [1, 5, 10, 15, 20, 100]

tasks:
  - name: Add specific users
    include_role:
      name: openldap
      tasks_from: add-user
    loop: "{{ user_ids }}"
    loop_control:
      loop_var: user_id
```

### Best Practices Summary

1. ✅ **Use `query()` for loops** - It's designed for this purpose
2. ✅ **Use `range() | list`** - Clear and Pythonic
3. ✅ **Test with small ranges first** - Avoid 100 failed attempts
4. ✅ **Use `loop_control`** - Give meaningful names to loop variables
5. ✅ **Add error handling** - Use `failed_when` and `ignore_errors` appropriately
6. ❌ **Avoid bare `lookup()`** - Unless you add `wantlist=True`
7. ❌ **Don't manually parse strings** - Let Ansible do it properly

### Quick Reference

```yaml
# ✅ CORRECT PATTERNS

# Pattern 1: range with list filter
loop: "{{ range(1, 101) | list }}"

# Pattern 2: query with sequence
loop: "{{ query('sequence', 'start=1 end=100') }}"

# Pattern 3: lookup with wantlist
loop: "{{ lookup('sequence', 'start=1 end=100', wantlist=True) }}"

# Pattern 4: simple list
loop: [1, 2, 3, 4, 5]

# Pattern 5: list variable
loop: "{{ my_list_variable }}"


# ❌ INCORRECT PATTERNS

# WRONG: lookup without wantlist
loop: "{{ lookup('sequence', 'start=1 end=100') }}"

# WRONG: range without list filter
loop: "{{ range(1, 101) }}"

# WRONG: string that looks like a list
loop: "1,2,3,4,5"
```

### Still Having Issues?

If you're still experiencing problems:

1. Check your Ansible version: `ansible --version` (minimum 2.9 recommended)
2. Enable verbose output: `ansible-playbook -vvv your-playbook.yml`
3. Verify the role path is correct
4. Check the task file being included exists
5. Review the `ansible/README.md` for examples

### Additional Resources

- [Ansible Loops Documentation](https://docs.ansible.com/ansible/latest/user_guide/playbooks_loops.html)
- [Ansible Lookup Plugins](https://docs.ansible.com/ansible/latest/plugins/lookup.html)
- [Ansible Query vs Lookup](https://docs.ansible.com/ansible/latest/user_guide/playbooks_lookups.html#query-and-lookup)
