# GitHub Contributors API

![Build status](https://github.com/aneksamun/github-contributors-api/actions/workflows/scala.yml/badge.svg)

Given organisation calculates the top contributors by counting thier total contributions over the repositories. The contributors are sorted in descending order to have most active on top.

```
[
  {
    "name": "user1",
    "contributions": 2017
  },
  {
    "name": "user2",
    "contributions": 1650
  },
  {
    "name": "user3",
    "contributions": 731
  }
]
```

### How to build?

- Clone project
- Build the project
```
sbt compile
```
### How to run locally?
An API is built on top of [GitHub REST API v3](https://developer.github.com/v3/). First you must [create a personal access token](https://docs.github.com/en/github/authenticating-to-github/creating-a-personal-access-token) if you do not have one and assign it to environment variable with the name **GH_TOKEN**. The complete list of variables can be found in table.   

**Table 1**: The list of available variables.
|Variable name         |Description                            |Required|Default                |
|----------------------|---------------------------------------|--------|-----------------------|
|SERVER_HTTP_PORT      |Server port                            |No      |8080                   |
|SERVER_CACHE_EXPIRY   |Cached result expiry                   |No      |30 minutes             |
|SERVER_IDLE_TIMEOUT   |Server response wait timeout           |No      |5 minutes              |
|SERVER_CLIENT_TIMEOUT |Client response wait timeout           |No      |1 minute               |
|GH_API_URL            |GitHub API URL                         |No      |https://api.github.com |
|GH_MAX_CONCURRENT     |Max concurrent requests to gather data |No      |100                    |
|GH_TOKEN              |Personal access token                  |Yes     |                       |
|CIRCUIT_MAX_FAILURES  |Circuit opening threshold              |No      |5                      |
|CIRCUIT_RESET_TIMEOUT |Circuit drop timeout                   |No      |10 seconds             |

To start service run
```
sbt run
```
Once service is running use an address http://localhost:8080/org/{org_name}/contributors where `{org_name}` is some organisation name.   

### Technology stack
- [scala 2.13.4](http://www.scala-lang.org/) as the main application programming language
- [cats](http://typelevel.org/cats/) to write more functional and less boilerplate code
- [cats-effect](https://github.com/typelevel/cats-effect) The Haskell IO monad for Scala
- [pureconfig](https://pureconfig.github.io/) for loading configuration files
- [refined](https://github.com/fthomas/refined) for type constraints avoiding unnecessary testing and biolerplate
- [scalatest](http://www.scalatest.org/) and [ScalaCheck](https://www.scalacheck.org/) for *unit and property based testing*
- [wiremock](http://wiremock.org/) to mock GitHub server
