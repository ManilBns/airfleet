#ifndef ALGORITHMS_H
#define ALGORITHMS_H

#include "list.h"

// Tri
Node* trierParCrashs(Node* head);    // du moins sûr au plus sûr
Node* trierParAutonomie(Node* head); // du plus long au plus court

// Statistiques
double moyenneCrashs(Node* head);
double moyenneAutonomie(Node* head);
int maxCrashs(Node* head);
int minCrashs(Node* head);

#endif
