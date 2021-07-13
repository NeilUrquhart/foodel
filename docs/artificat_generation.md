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
