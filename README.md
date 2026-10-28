
# Smart Delivery & Route Optimization Engine

## 1. Project Overview

This project implements a delivery route optimization engine using the
Strategy design pattern.

The system evaluates candidate delivery routes and selects a suitable route
according to a delivery request, business constraints, and the selected
optimization strategy.

## 2. Technologies

- Java 17
- Maven
- JUnit 5
- Git
- UML / PlantUML

## 3. Implemented Strategies

1. Fastest Route — prioritizes delivery time.
2. Cheapest Route — prioritizes total delivery cost.
3. Eco-Friendly Route — prioritizes lower environmental impact.
4. Balanced Route — combines multiple route criteria using weights.
5. Distance-First Route — prioritizes shorter distance.

## 4. Main Components

- RoutePlanner — context that executes the selected strategy.
- RouteStrategy — common interface for route selection algorithms.
- AbstractRouteStrategy — shared strategy implementation.
- StrategySelector — selects a strategy from delivery request properties.
- RouteEligibilityService — checks route constraints and calculates cost
  and effective delivery time.
- RouteComparisonService — compares multiple strategies for one request.

## 5. Business Constraints

The system checks route availability, prohibited route IDs, maximum budget,
maximum delivery time, and maximum carbon emissions.

If no candidate route satisfies the constraints, the system throws
NoSuitableRouteException.

## 6. Run the Project

Requirements: Java 17+ and Maven.

Compile:

    mvn clean compile

Run tests:

    mvn clean test

Run the demo:

Open `com.delivery.demo.Main` in IntelliJ IDEA and select Run.

## 7. Testing

JUnit tests cover model validation, route eligibility, strategy selection,
runtime strategy switching, invalid weights, unavailable routes, and
strategy comparison.

## 8. UML Diagrams

- `docs/class-diagram.png` — class diagram.
- `docs/sequence-diagram.png` — route planning sequence diagram.

## 9. SOLID Principles

- Single Responsibility Principle: route models, eligibility checks,
  strategy algorithms, selection, and comparison have separate roles.
- Open/Closed Principle: a new strategy can be introduced by implementing
  RouteStrategy or extending AbstractRouteStrategy.
- Dependency Inversion Principle: RoutePlanner depends on the RouteStrategy
  abstraction rather than concrete strategy classes.

## 10. Strategy vs Bridge

Strategy and Bridge can both use composition and interfaces, but they solve
different problems.

Strategy encapsulates alternative algorithms for performing a task.
In this project, each route strategy implements a different way of selecting
a delivery route.

Bridge separates an abstraction from its implementation so both can evolve
independently. For example, a notification abstraction could use different
delivery channels without being tied to a single channel implementation.

The main purpose of this project is to select and switch route optimization
algorithms, so Strategy is the appropriate pattern.
