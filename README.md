# LdapEntityManager

CRUD and search APIs with flexible data mapping using YAML.

## Learn

- [Proposal](./PROPOSAL.md)
- [Design](./DESIGN.md)
- [Ansible Integration](./ansible/README.md) - Examples and best practices for using with Ansible

## Prereqs

Minimum software requirements:
 * Git
 * Java SDK >= 11
 * Apache Maven >= 3
___________________________________________________________________________________
## Download & Install

From Apache GIT Fortress-Core Software Repo:
 https://github.com/shawnmckinney/LdapEntityManager

1. Clone the SNAPSHOT:
```bash
git clone  https://github.com/shawnmckinney/LdapEntityManager.git
```

2. Set Java and Maven home on machines.

3. From the project root folder, enter the following command:

```bash
mvn clean install
```

## TODO
...

## Setup LDAP

## Define Data Model

## Test

## Ansible Integration

This project includes Ansible playbooks and roles for automating LDAP entity management. See the [Ansible documentation](./ansible/README.md) for:

- Example playbooks for adding test users
- Troubleshooting guide for common loop-related errors
- Best practices for using Ansible with LDAP Entity Manager

**Quick Start with Ansible:**

```bash
# Build the project first
mvn clean install

# Run the quick test to verify loop syntax
ansible-playbook ansible/quick-test.yml

# Add 100 test users (example)
ansible-playbook ansible/add-test-users.yml
```

**Important**: If you encounter the error "Invalid data passed to 'loop'", see [ansible/TROUBLESHOOTING.md](./ansible/TROUBLESHOOTING.md) for solutions.
