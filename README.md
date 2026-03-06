# kong

Simple chat application using [Kong](https://github.com/Kong/kong) and [ChatGPT](https://chat.openai.com/chat).
and The frontend is built with [Tanstack](https://tanstack.com/) and [React](https://reactjs.org/).


## Features checklist
- [ ] app build/run outside of docker/IntelliJ

- [X] Chat
    - [ ] Free chat
    - [X] Chat with AI
    - [X] Chat history
- [ ] Chat Sessions
    - [X] Session creation
    - [X] Persistance
    - [X] Sessions list
    - [ ] optimistic update on session update
    - [ ] Session editing
    - [ ] Session deletion
- [ ] Tool calling
    - [ ] User Custom tools
- [ ] User management
    - [X] User creation (based on oauth discord/google)
    - [ ] proper cleanup of user data after logout
    - [ ] user meta from:
        - [X] discord
        - [ ] google
        - [ ] github



- [ ] LLM provuider checklist:
    - [X] Ollama
    - [ ] Google Gemini
    - [ ] Anthropic
    - [ ] OpenAI

## Building & Running

To build or run the project, use one of the following tasks:

| Task                                    | Description                                                          |
|-----------------------------------------|----------------------------------------------------------------------|
| `./gradlew test`                        | Run the tests                                                        |
| `./gradlew run`                         | Run the server                                                       |

If the server starts successfully, you'll see the following output:

```
2024-12-04 14:32:45.584 [main] INFO  Application - Application started in 0.303 seconds.
2024-12-04 14:32:45.682 [main] INFO  Application - Responding at http://0.0.0.0:8080
```
For frontend development, you can run the frontend server with:

``` bash
cd frontend
bun i
bun run dev
```
