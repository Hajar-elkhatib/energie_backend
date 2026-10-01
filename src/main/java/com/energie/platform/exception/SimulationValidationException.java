package com.energie.platform.exception;

import java.util.List;

/** Validation métier des données nécessaires avant d'envoyer une simulation au service IA. */
public class SimulationValidationException extends RuntimeException {
  private final String code;
  private final List<String> champs;

  public SimulationValidationException(String code, String message, List<String> champs) {
    super(message);
    this.code = code;
    this.champs = List.copyOf(champs);
  }

  public String getCode() { return code; }
  public List<String> getChamps() { return champs; }
}
