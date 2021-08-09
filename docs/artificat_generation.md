# Artifact Generation

## Background

Part of the website involves generating relevant artifacts for the user to download. This is useful because the user
does not need to, or need to know, how to compile the program from source. It also means they don't get bogged down with
too much of the technical detail and can get straight onto doing what they need to do.

Rather than generate executables and jars for every download request, one approach is to have GitHub do this for us.
GitHub provides a service called ["GitHub Actions"](https://github.com/features/actions). GitHub Actions is a service
that provides CI/CD for repositories on GitHub. This means a repository owner can test, build, and deploy their code all
from GitHub. This is similar to what projects such as Travis CI does.

GitHub actions, due to being run by Microsoft, integrates well with Azure; so provides a range of "runners". These
runners are just cloud VMs that we can run our code on.

For Foodel, the main reason to use GitHub actions is to automatically generate certain artifacts i.e:

- JAR files
- Windows exe files

This saves time generating the artifacts manually and means the artifacts on GitHub are always up-to-date.

The current actions setup on the Foodel repository integrate with [Maven](https://maven.apache.org). Maven is a
well-known tool to test, build, and package Java projects and can be used alongside GitHub Actions.

In short, the workflow is:
- Code is developed locally
- Code is commited and pushed to GitHub
- Once code is merged into the main / master branch:
  - Maven tests and builds packages(s)
  - It creates the JAR
  - Maven will fetch the JDK and will create the .exe file via Launch4J.
    - The executable will use the fetched JDK.
  - GitHub will create a 'release' under the 'latest' tag.
  - Foodel's installer will now fetch these resources when requested.


## Maven 

Maven is a Java tool for managing dependencies and packaging your code. Foodel uses Maven to manage its dependencies and to generate the JAR and .exe file that users install on their systems. 

Maven can be used with both Eclipse and IntelliJ. It can also be used on the command line.

They key part of using Maven is the "pom.xml" file.

## Creating Actions

GitHub's own [documentation](https://docs.github.com/en/actions) is a good place to start to get familiar with Actions.
Actions are defined in YAML files that define the steps to take.

To create an action, you just need to create a YAML file in a directory names `.github/workflows` in your repository.
You can then set up the relevant jobs.

These links are worth looking at:

- [Documentation](https://docs.github.com/en/actions)
- [Introduction to GitHub Actions](https://docs.github.com/en/actions/learn-github-actions/introduction-to-github-actions)
- [Quickstart](https://docs.github.com/en/actions/quickstart)
- [Actions Reference](https://docs.github.com/en/actions/reference)

## Current Workflow Explanation

The current workflow file can be found [here](https://github.com/NeilUrquhart/foodel/.github/workflows/release.yml) in
the `./github/workflows` directory of the [Foodel repository](https://github.com/NeilUrquhart/foodel).

The YAML file has comments explaining what each part does.

Currently, the action only runs against pushes and pull requests to the `main` branch. It then goes on to define two jobs: `test` and `deploy`. The test stage simply checks if things seem valid and that there is no issue. This should run on merge requests as well as pushes. 

The next stage actually compiles, packages, and releases the assets from maven. 

### "uses" Syntax

You'll see the `uses` syntax often with GitHub actions. These are plugins of sorts that you can use to do things quickly.

The ones used are:

- https://github.com/marvinpinto/action-automatic-releases
- https://github.com/actions/setup-java
- https://github.com/actions/checkout

These are just GitHub repos that integrate with actions nicely. To find more,
you can use [GitHub's market place](https://github.com/marketplace). There's
usally plenty of documentation about the actions in the repositories.
