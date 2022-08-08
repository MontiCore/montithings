# cd2proto

We piggiebacked the `MontiThingsGeneratorTool.java` in `montithings2cpp` by adding a new `GeneratorStep` (`GenerateProtobuf`).
This GeneratorStep utilizes the `montiarc.util.Modelfinder` and generates `.proto`-files for every `.cd`-file in the ModelPath.
After that, we call the protoc-compiler with the appropriate target-language (TBD: select language depending on Impl).
`protoc` compiles these into appropriate (optimized) files, that then can be integrated by the Impl-Files.
As the Impl-Files are a SSOT (but also SPOF) to the de-/serialization of protobuf-encoded Strings, this is the fastest and most failsafe way to implement it.
(Subject to change) We provide an inheritable Interface to the Impl-Files, so that de-/serialization is more convenient

## Getting started

* protoc >= 3.19
* libproto >= 3.19 (you may need to install from source)

## Add your files

- [x] [Create](https://docs.gitlab.com/ee/user/project/repository/web_editor.html#create-a-file) or [upload](https://docs.gitlab.com/ee/user/project/repository/web_editor.html#upload-a-file) files
- [x] [Add files using the command line](https://docs.gitlab.com/ee/gitlab-basics/add-file.html#add-a-file-using-the-command-line) or push an existing Git repository with the following command:

```
cd existing_repo
git remote add origin https://git.rwth-aachen.de/se-student/ss22/lectures/sle/student-projects/protobuf/cd2proto.git
git branch -M main
git push -uf origin main
```

## Integrate with your tools

- [ ] [Set up project integrations](https://git.rwth-aachen.de/se-student/ss22/lectures/sle/student-projects/protobuf/cd2proto/-/settings/integrations)

## Collaborate with your team

- [ ] [Invite team members and collaborators](https://docs.gitlab.com/ee/user/project/members/)
- [ ] [Create a new merge request](https://docs.gitlab.com/ee/user/project/merge_requests/creating_merge_requests.html)
- [ ] [Automatically close issues from merge requests](https://docs.gitlab.com/ee/user/project/issues/managing_issues.html#closing-issues-automatically)
- [ ] [Enable merge request approvals](https://docs.gitlab.com/ee/user/project/merge_requests/approvals/)
- [ ] [Automatically merge when pipeline succeeds](https://docs.gitlab.com/ee/user/project/merge_requests/merge_when_pipeline_succeeds.html)

## Test and Deploy

Use the built-in continuous integration in GitLab.

- [ ] [Get started with GitLab CI/CD](https://docs.gitlab.com/ee/ci/quick_start/index.html)
- [ ] [Analyze your code for known vulnerabilities with Static Application Security Testing(SAST)](https://docs.gitlab.com/ee/user/application_security/sast/)
- [ ] [Deploy to Kubernetes, Amazon EC2, or Amazon ECS using Auto Deploy](https://docs.gitlab.com/ee/topics/autodevops/requirements.html)
- [ ] [Use pull-based deployments for improved Kubernetes management](https://docs.gitlab.com/ee/user/clusters/agent/)
- [ ] [Set up protected environments](https://docs.gitlab.com/ee/ci/environments/protected_environments.html)

***

# Editing this README

When you're ready to make this README your own, just edit this file and use the handy template below (or feel free to structure it however you want - this is just a starting point!).  Thank you to [makeareadme.com](https://www.makeareadme.com/) for this template.

## Suggestions for a good README
Every project is different, so consider which of these sections apply to yours. The sections used in the template are suggestions for most open source projects. Also keep in mind that while a README can be too long and detailed, too long is better than too short. If you think your README is too long, consider utilizing another form of documentation rather than cutting out information.


## Description
Let people know what your project can do specifically. Provide context and add a link to any reference visitors might be unfamiliar with. A list of Features or a Background subsection can also be added here. If there are alternatives to your project, this is a good place to list differentiating factors.

## Visuals
Depending on what you are making, it can be a good idea to include screenshots or even a video (you'll frequently see GIFs rather than actual videos). Tools like ttygif can help, but check out Asciinema for a more sophisticated method.

## Installation

1. use montithings2cpp dependency in your project
#### Development
you may skip the tests with `-Dmaven.skip.tests=true`
and building the docker container with `Dexec.skip`
* `mvn clean install` the `cd2proto` artifact locally
* `mvn clean install` the `cd2cpp` artifact locally
* `mvn clean install` the `montithings2cpp` artifact locally
* eventually `mvn clean install` the `montithings` artifact locally
* see 1.

Within a particular ecosystem, there may be a common way of installing things, such as using Yarn, NuGet, or Homebrew. However, consider the possibility that whoever is reading your README is a novice and would like more guidance. Listing specific steps helps remove ambiguity and gets people to using your project as quickly as possible. If it only runs in a specific context like a particular programming language version or operating system or has dependencies that have to be installed manually, also add a Requirements subsection.

## Usage
Use examples liberally, and show the expected output if you can. It's helpful to have inline the smallest example of usage that you can demonstrate, while providing links to more sophisticated examples if they are too long to reasonably include in the README.


## Roadmap
If you have ideas for releases in the future, it is a good idea to list them in the README.

## Authors and acknowledgment
Sebastian Grabowski
Andre Fugmann
Merlin Freiherr von Rössing
Danyls Tchekambou Ngongang
Tim Nebel

## Project Status
Proof of Concept - No exhaustive coverage (e.g. both-way associations don't work yet.)