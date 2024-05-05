# Salmonberry
Salmonberry is an experimental roguelite server system written in Java. This is more akin to a scratch repository, and as a result, Eclipse is the required IDE.

---

- Q: Why Java?
- A: Portability and familiarity! Most of my programming experience, professionally and recreational, is object-oriented - primarily C++ and C#. While I could use NodeJS with something like typescript or similar since this is more along the lines of a webapp/webserver, I'm not as familair with them, and while I use C++ and C# more, the platform flexibility of Java makes it easier for myself and other people to develop on and for a wide range of devices and device architecures without as much fiddling.
<br/><br/>
- Q: Why does this require eclipse?
- A: By specifying an IDE, I can make some assumptions about project configuration which I find useful for network development and iteration. A more generic system could easily be argued for later! And Eclipse specifically? Well, It's robust, free, and functional enough! New feature additions in the last few years have made it something I've come to enjoy using more. It may not be my preferred IDE, but it's a reliable IDE, and I highly recommend [donating to the eclipse foundation](https://www.eclipse.org/donate/) if it's something you enjoy using!
<br/><br/>
- Q: What do you use for the HTTP server?
- A: I use nanoHTTPD, which is a lovely little library that provides a lot of functionality without having a bunch of other dependencies!
<br/><br/>
- Q: How do I build this jar in Eclipse?
- A: After importing the project, head to File > Export. Select the Java section, then 'Runnable Jar File'. Select the launch configuration for salmonberry's main, and then ensure 'Extract required libraries into generated JAR'.
