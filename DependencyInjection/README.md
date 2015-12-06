# Dependency Injection in Playframework 2.4

This project illustrates how to use DI in your Play project, be it JSR 300 / Guice or (better) compile-time / 
constructor DI.

The `Shared` project contains an imaginary database client that you are trying to integrate into your project using DI
components. Both `Jsr330` and `CompileTime` projects show how to do that such that the client is instantiated, testable,
and stopped on each application reload (in dev mode).

The accompanying post can be found on my blog: http://mariussoutier.com/blog/2015/12/06/playframework-2-4-dependency-injection-di/
