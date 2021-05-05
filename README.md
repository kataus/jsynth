# jSynth

A virtual analog synthesizer written in Java.

## Specs

The functionality is somehow limited at the moment and includes:

### Synthesizer functions

- 4 different oscillators (there are 5 classes in the `oscillator` package, but white noise is not used anywhere)
- 2 effects: delay and low-pass (again, there are some more in the `fx` package, but they are still WIP)
- a simple envelope generator (only decay is supported at the moment)
- a 16-note sequencer

### Drum machine functions

- a 16-note sequencer with max. 4 samples per note

### Input/Output

- a 2-bus FX processor
- (virtually) unlimited mixers
- a synth rack with 4 synthesizers + 2 drum machines

### API

- WebSocket API

## Implementation details

- to ensure precise tempo accuracy, buffer readouts are currently used to sync the application
- all sound producers run in a single thread and are iterated upon with every tick 
- API and Synth Rack communicate via a PubSub message broker

## Libraries

This repository includes a local copy of the berndporr/iirj library with several important changes to its code. A proper
fork will be made in the foreseeable future.

Another library included in this repository is Knob.js (it also required a bugfix).

## Building and running the Java application

Usual stuff with a couple of notes:

- no additional configuration is necessary at the moment, but since the WS port is hardcoded, it has to be free
  system-wide, otherwise the application won't run
- for licensing reasons, the `resources/drums` directory is currently excluded from the VCS

## Building and running the web application

WIP.

## Testing

No tests have been written so far.

## License

Not decided yet.