<!-- (c) https://github.com/MontiCore/monticore -->
# Contributing to MontiThings

At the very heart of each software, there is always code. We do not write this code for the software to work, but to evolve! In other words, we write code for others (e.g., the future you) to read. As we believe in collaboration, we want to establish a [workflow and principles](#table-of-contents) that support that.

> Care about the person that comes after you; it might be you! ([Miško Hevery][vid-Psychology of Testing])

If you would like to contribute to this project, read and know [*all given information and sources*](#table-of-contents) provided by this file and the project owners!

## Table of Contents

1. [Workflow](#workflow)
1. [Code Style](#code-style)

## Workflow

In general:
1. Fork the project.
1. Create feature branch.
1. Commit improvement.
1. Integrate *develop*.
1. Create pull request.

### Git

This project's source code is managed with [Git] and is hosted on [RWTH's GitLab].

Use [merge][doc-git-merge] instead of [rebase][doc-git-rebase]!

#### Commit Messages

Consult this [article][doc-git-commit-msg] on how to write good Git commit messages.

Use `See #{issue-number}` at the end of commit message to reference issue. Use `Fixes #{issue-number}`, `Closes #{issue-number}`, or `Resolves #{issue-number}` only in *merge commits to develop*.

### GitFlow

Follow the [GitFlow][doc-gitflow] branching model with the following configuration:

```
[gitflow "branch"]
  develop = develop
  master = master
[gitflow "prefix"]
  feature = feature/
  release = release/
  hotfix = hotfix/
  support = support/
  versiontag =
```

We strongly recommend using [SmartGit] or at least [git-flow].

Use the *dash-notation* with *type-of-change* first and *all-lower-case* letters for feature branches. For example:

```
feature/new-feature-plugin-x
feature/add-integration-test-for-x
feature/update-dependencies-version
feature/fix-bug-x
feature/amend-api-x
feature/refactor-delegate-x-responsibility
```

Useful type-of-change prefixes are (do not use abbreviations): `new-feature`, `add`, `update`, `upgrade`, `fix`, `augment`, `amend`, `refactor`, `rename`, `remove`.

**Additional Note:**
* Never integrate two feature branches!
* There must be no long-living feature branches!

### Pre- and Post-Push

* **Pre-Push**:
    - [ ] Commits have [proper messages](#git-and-gitflow)
    - [ ] [Run all unit tests](README.md)
        - [ ] All checks pass
        - [ ] There are no ignored checks
        - [ ] Changes are sufficiently [covered by checks](#checks-first-development)
    - [ ] [Coding conventions](#code-style) are adhered to
        - [ ] Code is cleaned up (e.g., correct formatting, no unused imports, no commented-out code)
        - [ ] Code and dependency analysis yield no major issues
        - [ ] All public API has [Javadoc](#javadoc-every--interface)
    - [ ] No TODOs left
* **Post-Push**:
    - [ ] Check that the build pipeline passes

### Pull Request and Code Reviews

The *master* and *develop* branches are read-only! So the only option is to [create a pull request][doc-pull-request] for your *feature* branch. Name your pull request with your *branch name*.

Review code with all [design](#design-principles), [test](#checks-first-development), and [code style](#code-style) principles of this readme in mind. Further, make use of all the information you can get; e.g., investigate the CI pipeline, run checks with coverage, perform code analysis, calculate metrics, etc.

For code reviews, a good starting point is the [check list for pre- and post-push](#pre--and-post-push).

Read these [recommendations][doc-code-reviews].

### Tools We Use

* [Junit 5] as the main testing framework
    - [Quick Start][doc-junit5-quick-start]
    - [User Guide][doc-junit5-user-guide]
* [AssertJ] to write checks that are readable
    - [Quick Start][doc-assertj-quick-start]
    - [Building a DSL][doc-assertj-custom-assertions]
    - [Code Examples][doc-assertj-code-examples]
    
### Check Conventions

* Some things to keep in mind
    - Use a domain-specific language ([AssertJ] helps)
    - [Keep cause and effect clear][art-tot-cause-and-effect]
    - Single concept per check
        - The check should fail only for one reason
        - One assert per check (in general)
    Checks must be [***F.I.R.S.T.***][Clean Code]
    - Tests should follow [Given-When-Then] structure
* Classes that comprise Unit Test checks...
    - must end with `Test` suffix.
    - must be located in `src/test/java/` and *same package* as the tested class.
* Use [JUnit 5]'s...
    - [nested tests][doc-junit5-nested-tests] to group checks meaningfully.
    - [`assertAll()`][doc-junit5-assertall] instead of AssertJ's `SoftAssertions`.
    - [assumptions][doc-junit5-assumptions] to check theories.
    - [dynamic tests][doc-junit5-dynamic-tests] check theories; e.g., checking [properties][art-testing-properties] for multiple inputs.
* Use [AssertJ]'s [assertions][doc-assertj-code-examples] only.
    - Introduce [custom assertions][doc-assertj-custom-assertions] for better readability.
    
[**Back to TOC**](#table-of-contents)

## Code Style

Most importantly, write for [readability][The Art of Readable Code]. Consider [Clean Code] and [Effective Java]. 

> Clean code is simple and direct. Clean code reads like well-written prose. Clean code never obscures the designer’s intent but rather is full of crisp abstractions and straightforward lines of control. ([Grady Booch][Clean Code])

Follow the [SE Style Guide]. We recommend using [IntelliJ] or [eclipse] for this project; therefore, we provide the [code style configuration][SE Code Style Config]. Most importantly, **indent with 2 spaces!** (to tabs allowed).

### The Intent Has to be Graspable in 5 Seconds

> Good code reveals intent. ([Kostadin Golev][art-on-writing-code-well])

The reader of your *method* should grasp the *WHAT?* in **5 seconds**! In other words, make methods small and use meaningful, intention-revealing naming. [Separate intention from implementation][art-function-length]!

> Comments Do Not Make Up for Bad Code ([Robert C. Martin][Clean Code])

For a class the 5 seconds deadline still holds!

### Javadoc Every `public` Interface

> There is nothing quite so helpful and satisfying as a well-described public API. ([Robert C. Martin][Clean Code])

Write informative [Javadocs][Javadoc]; explaining the intent, clarifying [restrictions](#use-preconditions), and warning of consequences. Link other program elements. State preconditions for parameters. Explain return values.

Note: Do not add `@author`. The team is responsible for the code; not one person. 

### Make Values `final`

All method *parameters* and most *local variables* are values; i.e., use the `final` keyword.

### Never Write `equals()` and `hashCode()` Yourself

Never write those methods yourself! All IDEs can generate those methods for you. In IntelliJ, use the Java7+ template.

### There Must Be No Commented-out Code

Delete those leftovers!

### Don't Use Private

`private` variables and methods make it harder to derive from a class because it forces you to copy/paste code related to the `private` attributes to the derived subclass - even if you don't want then overriden. Use `protected` instead to prevent unrelated classes from accessing the variable.

### Use Logger only

Never use `System.out.print`. Make use of the logging framework.

Some remarks:
* Use logging sparingly.
* Do not log in utility classes.
* Use levels:
    - `ERROR`: extra information (in addition to an Exception) about errors that will halt execution
    - `WARN` : potential usage errors that should not halt execution
    - `INFO` : stuff the users might want to know but not by default
    - `DEBUG`: stuff that might be helpful for the developer for debugging

[**Back to TOC**](#table-of-contents)

Remark: This guide is based on the guide to the READER project provided by @Andrej.Dyck from Software Construction Group, RWTH Aachen University

[RWTH's GitLab]: https://git.rwth-aachen.de/
[RWTH's GitLab Pipeline]: https://git.rwth-aachen.de/

[SE Code Style Config]: https://sselab.de/lab2/private/svn/se-conventions/trunk/

[Git]: https://git-scm.com/
[SmartGit]: http://www.syntevo.com/smartgit/
[git-flow]: http://skoch.github.io/Git-Workflow/
[Junit 5]: http://junit.org/junit5/
[AssertJ]: http://joel-costigliola.github.io/assertj/
[Mockito]: http://site.mockito.org/
[IntelliJ]: https://www.jetbrains.com/idea/
[Log4j 2]: https://logging.apache.org/log4j/2.x/
[eclipse]: https://www.eclipse.org/

[Google Java Style Guide]: https://google.github.io/styleguide/javaguide.html
[SE Style Guide]: https://sselab.de/lab1/user/project/se-conventions/
[Javadoc]: http://www.oracle.com/technetwork/articles/java/index-137868.html

[doc-git-merge]: https://www.atlassian.com/git/tutorials/git-merge
[doc-git-rebase]: https://www.atlassian.com/git/tutorials/rewriting-history#git-rebase
[doc-git-commit-msg]: http://chris.beams.io/posts/git-commit/
[doc-gitflow]: http://nvie.com/posts/a-successful-git-branching-model/
[doc-pull-request]: https://www.atlassian.com/git/tutorials/making-a-pull-request
[doc-code-reviews]: http://blog.codefx.org/techniques/code-reviews-disy-part-3/#Recommendations
[doc-junit5-quick-start]: https://dmitrij-drandarov.github.io/JUnit5-Quick-Start-Guide-and-Advanced/
[doc-junit5-user-guide]: http://junit.org/junit5/docs/current/user-guide/
[doc-junit5-nested-tests]: http://junit.org/junit5/docs/current/user-guide/#writing-tests-nested
[doc-junit5-assertall]: http://junit.org/junit5/docs/current/user-guide/#writing-tests-assertions
[doc-junit5-assumptions]: http://junit.org/junit5/docs/current/user-guide/#writing-tests-assumptions
[doc-junit5-dynamic-tests]: http://junit.org/junit5/docs/current/user-guide/#writing-tests-dynamic-tests
[doc-assertj-quick-start]: http://www.baeldung.com/introduction-to-assertj
[doc-assertj-custom-assertions]: http://joel-costigliola.github.io/assertj/assertj-core-custom-assertions.html
[doc-assertj-code-examples]: https://github.com/joel-costigliola/assertj-examples/tree/master/assertions-examples/src/test/java/org/assertj/examples
[doc-assertj-softassertions]: http://joel-costigliola.github.io/assertj/assertj-core-features-highlight.html#soft-assertions
[doc-java-optional]: https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html

[art-test-first]: https://8thlight.com/blog/uncle-bob/2013/09/23/Test-first.html

[art-giving-up-on-tdd]: http://blog.cleancoder.com/uncle-bob/2016/03/19/GivingUpOnTDD.html
[art-tdd-not-a-slow-down]: https://8thlight.com/blog/uncle-bob/2013/03/11/TheFrenziedPanicOfRushing.html
[art-tdd-makes-you-happy]: https://8thlight.com/blog/chris-jordan/2016/04/13/happiness-and-tdd.html
[art-tests-give-confidence]: http://enterprisecraftsmanship.com/2016/06/01/unit-tests-value-proposition/
[art-tot-cause-and-effect]: https://testing.googleblog.com/2017/01/testing-on-toilet-keep-cause-and-effect.html
[art-testing-properties]: https://fsharpforfunandprofit.com/pbt/
[art-writing-testable-code]: http://misko.hevery.com/attachments/Guide-Writing%20Testable%20Code.pdf
[art-how-to-think-about-the-new-operator]: http://misko.hevery.com/2008/07/08/how-to-think-about-the-new-operator/
[art-on-writing-code-well]: http://www.kgolev.com/on-writing-code-well/
[art-function-length]: https://martinfowler.com/bliki/FunctionLength.html
[art-null-object-pattern]: https://en.wikipedia.org/wiki/Null_Object_pattern

[vid-Psychology of Testing]: https://www.youtube.com/watch?v=pqomi6W4AJ4
[vid-The Three Laws of TDD]: https://www.youtube.com/watch?v=qkblc5WRn-U
[vid-Boundaries]: https://www.destroyallsoftware.com/talks/boundaries
[vid-The Principles of Clean Architecture]: https://www.youtube.com/watch?v=o_TH-Y78tt4
[vid-SOLID Principles of Object-oriented and Agile Design]: https://www.youtube.com/watch?v=TMuno5RZNeE
[vid-How To Design A Good API and Why it Matters]: https://www.youtube.com/watch?v=heh4OeB9A-c
[vid-The Clean Code Talks - Dont Look For Things]: https://www.youtube.com/watch?v=RlfLCWKxHJ0

[Given-When-Then]: https://martinfowler.com/bliki/GivenWhenThen.html
[Growing Object-Oriented Software, Guided by Tests]: https://www.amazon.de/dp/0321503627
[Clean Code]: https://www.amazon.de/dp/0132350882
[Dependency Injection]: https://en.wikipedia.org/wiki/Dependency_injection
[Dependency Inversion Principle]: https://en.wikipedia.org/wiki/Dependency_inversion_principle
[Effective Java]: https://www.amazon.de/dp/0321356683
[S.O.L.I.D.]: https://web.archive.org/web/20150906155800/http://www.objectmentor.com/resources/articles/Principles_and_Patterns.pdf
[The Art of Readable Code]: https://www.amazon.de/dp/0596802293
