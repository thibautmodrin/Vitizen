#!/bin/bash

# Définir les chemins
SOURCE_DIR="vosk-model-small-fr-pguyot-0.3"
TARGET_DIR="app/src/main/assets/vosk-model-small-fr-pguyot-0.3"

# Créer la structure de dossiers
mkdir -p "$TARGET_DIR"/{am,conf,graph/phones}

# Copier les fichiers dans la structure appropriée
# AM
cp "$SOURCE_DIR"/final.mdl "$TARGET_DIR"/am/

# CONF
cp "$SOURCE_DIR"/model.conf "$TARGET_DIR"/conf/
cp "$SOURCE_DIR"/mfcc.conf "$TARGET_DIR"/conf/

# GRAPH
cp "$SOURCE_DIR"/HCLr.fst "$TARGET_DIR"/graph/
cp "$SOURCE_DIR"/Gr.fst "$TARGET_DIR"/graph/
cp "$SOURCE_DIR"/disambig_tid.int "$TARGET_DIR"/graph/

# GRAPH/PHONES
cp "$SOURCE_DIR"/word_boundary.int "$TARGET_DIR"/graph/phones/

# Copier le README
cp "$SOURCE_DIR"/README "$TARGET_DIR"/

echo "Organisation des fichiers terminée !" 