#!/bin/sh

function handler() {
    EVENT_DATA=$1
    
    ./dottiesbot "$EVENT_DATA"

    RESPONSE="Echoing request: '$EVENT_DATA'"
    echo $RESPONSE
}
