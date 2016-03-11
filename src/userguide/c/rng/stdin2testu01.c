/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Utility for simple interfacing with the "TestU01" library:
 *  http://simul.iro.umontreal.ca/testu01/tu01.html
 *
 * It reads from its standard input an infinite sequence of 32-bits
 * integers and runs one of the test suites "SmallCrush", "Crush" or
 * "BigCrush".
 * "TestU01" writes its report to standard output.
 */

#include <stdint.h>
#include <unistd.h>
#include <string.h>

#include <testu01/unif01.h>
#include <testu01/bbattery.h>
#include <testu01/util.h>

#define TU_S "SmallCrush"
#define TU_C "Crush"
#define TU_B "BigCrush"
#define BUFFER_LENGTH 256

typedef struct {
  unsigned long buffer[BUFFER_LENGTH];
  uint32_t index;
} StdinReader_state;

unsigned long nextInt(void *par,
                      void *sta) {
  StdinReader_state *state = (StdinReader_state *) sta;
  if (state->index >= BUFFER_LENGTH) {
    /* Refill. */
    fread(state->buffer, sizeof(unsigned long), BUFFER_LENGTH, stdin);
    state->index = 0;
  }

  uint32_t random = state->buffer[state->index];
  ++state->index; /* Next request. */

  return random;
}

double nextDouble(void *par,
                  void *sta) {
  return nextInt(par, sta) / 4294967296.0;
}


static void dummy(void *sta) {
  printf("N/A");

  return;
}

unif01_Gen *createStdinReader(void) {
   unif01_Gen *gen;
   StdinReader_state *state;
   size_t len;
   char name[60];

   state = util_Malloc(sizeof(StdinReader_state));

   gen = util_Malloc(sizeof(unif01_Gen));
   gen->state = state;
   gen->param = NULL;
   gen->Write = dummy;
   gen->GetU01 = nextDouble;
   gen->GetBits = nextInt;

   strcpy(name, "stdin");
   len = strlen(name);
   gen->name = util_Calloc(len + 1, sizeof (char));
   strncpy(gen->name, name, len);

   // Read binary input.
   freopen(NULL, "rb", stdin);
   state->index = BUFFER_LENGTH;

   return gen;
}

void deleteStdinReader(unif01_Gen *gen) {
   gen->state = util_Free(gen->state);
   gen->name = util_Free(gen->name);
   util_Free(gen);
}

int main(int argc,
         char **argv) {
  unif01_Gen *gen = createStdinReader();
  char *spec = argv[1];

  if (argc < 2) {
    printf("[ERROR] Specify test suite: '%s', '%s' or '%s'\n", TU_S, TU_C, TU_B);
    exit(1);
  } else if (strcmp(spec, TU_S) == 0) {
    bbattery_SmallCrush(gen);
  } else if (strcmp(spec, TU_C) == 0) {
    bbattery_Crush(gen);
  } else if (strcmp(spec, TU_B) == 0) {
    bbattery_BigCrush(gen);
  } else {
    printf("[ERROR] Unknown specification: '%s'\n", spec);
    exit(1);
  }

  deleteStdinReader(gen);
  return 0;
}
