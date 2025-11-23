# Lazy Code Motion

This repository contains an implementation of **Lazy Code Motion (LCM)** using four compiler passes. It processes programs written in **SIMPL**, a lightweight educational programming language. The project is built on top of a class assignment for constructing a SIMPL parser.

https://github.com/user-attachments/assets/9d3bc408-02bd-4908-aa16-5580531e3f7e

## Features

- Support for:
  - **Assignment statements**
  - **If conditionals**
  - **While loops**
- Full implementation of **Lazy Code Motion** with:
  - ANT-in / ANT-out
  - AV-in / AV-out
  - Earliest / Latest computation
  - Usedin, Usedout

## Worklist Algorithm Implementation

This repository also includes an implementation of the **Worklist Algorithm** following the **Strategy Design Pattern**.
Relevant Files:
- `Worklist.java`
- `lcm_worklist.java`

## Visualization Tool

To better demonstrate the impact and efficiency of the Worklist-based implementation, a simple **HTML + JavaScript visualization tool** is included. Relevant Files
- `index.html`
- `logVisualizerTable.js`
- `lcm_worklist_log.txt, lcm_worklist_order_log.txt (log files created on running the parser)`

The visualization:

- Displays all relevant LCM data-flow sets  
  (*ANTin, ANTout, AVin, earliest, latest*, etc.)
- Shows step-by-step updates of these sets as the algorithm runs
- Reads from a **log file** that records all newly computed values
- Displays the processing order of CFG nodes according to the worklist algorithm
- Highlights the next computation by updating the corresponding table cells in real time
