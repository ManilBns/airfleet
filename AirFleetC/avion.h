#ifndef AVION_H
#define AVION_H

#define MAX_STRING 50

typedef struct {
    int id;
    char fabricant[MAX_STRING];
    char modele[MAX_STRING];
    int capacite;
    int autonomie;
    int crashs;
    int anneeService;
} Avion;

#endif
