# jSynth

A virtual analog synthesizer written in Java.

## Specs

The functionality is somehow limited at the moment.

### Synthesizer

- 4 different oscillators (there are 5 classes in the `oscillator` package, but white noise is not used anywhere)
- 2 effects: delay and low-pass (again, there are some more in the `fx` package, but they are still WIP)
- simple envelope generator (only decay is supported at the moment)
- 16-note sequencer

### Drum machine

- 16-note sequencer with max. 4 samples per note

### Input/Output

- 2-bus FX processor
- (virtually) unlimited mixers
- synth rack with 4 synthesizers + 2 drum machines

### API

- WebSocket API

## Implementation details

- to ensure precise tempo accuracy, buffer readouts are currently used to sync the application
- all sound producers are running in a single thread and are iterated upon on each tick
- API and Synth Rack are communicating via a PubSub message broker

## Libraries

This repository includes a local copy of the berndporr/iirj library with several important changes in its code. A proper
fork will be made in the foreseeable future.

## Building and running the Java application

Usual stuff with a couple of notes:

- no additional configuration is necessary at the moment, but since the WS port is hardcoded, it has to be free
  system-wide, otherwise the application won't run
- for licensing reasons, the resources/drums directory is currently excluded from VCS

## Building and running the web application

WIP.

## Testing

No tests have been written so far.

## License

Not decided yet.