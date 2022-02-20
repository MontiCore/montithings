# Frequently Asked Questions

**Q:** "CMake cant find my compiler. Whats wrong?"<br>
**A:** "Most likely your environment variables are wrong. On Windows start the terminal window using
Visual Studio's variable script under `C:\Program Files (x86)\Microsoft Visual Studio\2019\Community\VC\Auxilliary\Build\vcvarsall.bat x64`
(your path might be a little different depending on you installation location and Visual Studio version).

**Q:** "`mvn clean install` fails with error `The forked VM terminated without properly saying goodbye. VM crash or System.exit called?`. Why?"<br>
**A:** Most likely your terminal couldn't handle that much output. Try to either build MontiThings using Intellij or redirect the output to a file: `mvn clean install > output.log 2>&1`

**Q:** "My terminal says 'Killed' when running `mvn clean install`. Why?" <br>
**A:** Probably you don't have enough memory. Check it using `dmesg -T| grep -E -i -B100 'killed process'`.

**Q:** "Docker says something like 'denied: access forbidden'"<br>
**A:** You likely tried to execute an image that is provided via (our internal) the Docker registry from RWTH Aachen University's GitLab instance.
In most cases you can pull the images from Docker Hub by replacing the `registry.git.rwth-aachen.de/monticore/montithings/core` by just `montithings`.
In case you have access to our internal repository, you likely forgot to log in first.
Call `docker login registry.git.rwth-aachen.de` and the credentials you
use to log into this GitLab.

**Q:** "I don't know my credentials to RWTH Aachen's GitLab's internal Docker registry. I always log in through the RWTH single-sign on"<br>
**A:** "You can find your username by clicking on your icon in the top right corner. The dropdown should tell
you your username (something like `@christian.kirchhof`). If you haven't set a differnet password for GitLab
your password is most likely the password you use everywhere else to login with you TIM id (TIM id has the
form `xy123456`). In case you have never logged in using a manually set password, you maybe need to first
[set a password (not publicly available)][password].

**Q:** "I cant execute the binary. It says something like 'cannot execute binary file' (or something similar)"<br>
**A:** You most likely compiled the binary using Docker and are now trying to execute it outside of the container.
As the different operating systems use different formats for their binaries, this doesn't work. If you have some
time to waste, you can read more about the different file formats on Wikipedia:
[ELF][elf] (Linux), [Mach-O][mach-o] (macOS), [Portable Executable][portable-executable] (Windows).

**Q:** "I can't execute the binary. It says something like `-bash: ./hierarchy.Example: No such file or directory`. But I can clearly see the file when running `ls`.<br>
**A:** You likely compiled the binary using Docker and are now trying to call it from outside the container.
Please remove the `build/bin` folder: from `target/generated-sources` call `sudo rm -rf build` (you need `sudo` because the folder doesn't belong to you if its built with Docker). If you don't have `sudo` rights, you can also go back inside the Docker container (`docker run -it --rm -v $PWD:$PWD -w $PWD montithings/mtcmake`) and remove the folder from within the container. After removing the folder, please rebuild the project without using Docker.

**Q:** "Why do I get the following error message (or a similar error):
```
* What went wrong:
An exception occurred applying plugin request [id: 'de.set.ecj', version: '1.4.1']
> Failed to apply plugin 'de.set.ecj'.
   > Could not create plugin of type 'EclipseCompilerPlugin'.
      > Could not generate a decorated class for type EclipseCompilerPlugin.
         > org/gradle/jvm/toolchain/JavaToolChain
```
**A:** Gradle unfortunately has many breaking changes between its versions. Not all plugins are updated in a timely manner by their maintainers. You're probably using Gradle in version 7. Please use the lastest available version 6 release: https://gradle.org/releases/

**Q:** "Why am I getting the following error?

Maven:
```
[ERROR] An internal error occured.
org.codehaus.groovy.control.MultipleCompilationErrorsException: startup failed:
General error during conversion: Unsupported class file major version 61
```
Gradle:
```
> startup failed:
  General error during semantic analysis: Unsupported class file major version 61
```
**A:** Your Java version is too new. Please use JDK 8, 11, or 14. Other versions are not checked by the CI pipeline.


**Q:** Why am I getting the following error in PowerShell?
```
Error downloading object: docs/Banner.png (a631659): Smudge error: Error downloading docs/Banner.png (a6316593e8f3cafc22350d1799235e871ddacd061f888e875f33c496cff83f3c): batch request: Permission denied, please try again.
Permission denied, please try again.
...
warning: Clone succeeded, but checkout failed.
You can inspect what was checked out with 'git status'
and retry with 'git restore --source=HEAD :/'
```
**A:** Apparantly, there is a problem with git lfs and PowerShell, if you use an SSH key that has a passphrase.
Please use a different shell (such as Git Bash on Windows; see [this answer](https://github.com/git-lfs/git-lfs/issues/3216#issuecomment-750920515) on GitHub) or use an SSH key without a passphrase. 
