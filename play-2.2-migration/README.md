# Building and Composing Actions in Playframework 2.2

## Intro

The way Actions are built and composed has changed in Playframework 2.2. This repository features
sample code that will guide you through the new Action-style. But please read the accompanying
blog post first: http://www.mariussoutier.com/blog/2013/09/17/playframework-2-2-action-building-action-composition/

## EssentialAction vs. Action

It's not entirely clear how to choose between EssentialAction, EssentialFilter, ActionBuilder, and
Action.

* Choose `EssentialFilter` when you want to build application-wide rules that ignore the request body
* Choose `EssentialAction` when you want to build action-specific rules that ignore the request body
  *or* if you want to use function composition *or* if you want to stream the request body
* Choose `ActionBuilder` to easily build and compose actions that handle both `Result` and
  `Future[SimpleResult]` on the fly,
  *or* if you want to add properties to the request (see `WrappedRequest`)
* Use `Action` if you need the body and want more control than `ActionBuilder` gives you

![EssentialAction vs Action](tutorial/WhichActionToChoose.png)

## ActionBuilding Internals

Are you curious to learn more? This is an overview of how Actions are really built, and how that
differs from invoking an Action.

![Action Building](tutorial/ActionBuilding.png)
