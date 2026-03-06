# TeaStore Server Setup Documentation

## Purpose

This document records the steps used to access the UNC Charlotte
research server (`cci-marco`) and configure the environment for running
LLM-based robustness testing experiments for TeaStore.

## Server Access

SSH access is required to use the university compute resources.

Command used:

    ssh htigulla@cci-marco

Authentication uses: - **Username:** NinerNET username (htigulla) -
**Password:** NinerNET password

The connection must be made either: - from the UNC Charlotte network,
or - while connected to the UNC Charlotte VPN.

## Python Environment Setup

A Python virtual environment was created on the server.

Command:

    python3 -m venv ~/python3

This creates a dedicated Python environment for experiment scripts.

## Updating PATH

The following line was added to the end of the `.profile` file:

    PATH="/home/htigulla/python3/bin:$PATH"

This ensures that the virtual environment Python binaries are
automatically available after login.

Verification command:

    echo $PATH

Expected output includes:

    /home/htigulla/python3/bin

## Installing Ollama Python Client

The Ollama Python library was installed inside the virtual environment.

Command:

    pip3 install ollama

This library allows Python scripts to interact with the Ollama LLM
service running on the server.

## Testing Ollama Connectivity

A simple Python script (`test.py`) was created to confirm that the
Ollama API can be accessed.

Example code:

``` python
import ollama

response = ollama.chat(
    model='qwen3:14b',
    messages=[
        {'role': 'user', 'content': 'Why is the sky blue?'}
    ]
)

print(response)
```

Running the script:

    python3 test.py

Successful execution confirmed that: - the Ollama service is reachable -
the Python client is working - models can be invoked from Python.

## Models Available on the Server

The following models were listed using:

    ollama list

Available models:

-   gemma2:27b
-   gemma2:9b
-   gemma:7b
-   gemma2:2b
-   gemma3:12b
-   qwen3:14b

Additional larger models were later pulled for experimentation.
