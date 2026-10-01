-- À exécuter une seule fois sur la base PostgreSQL de production, avant le redémarrage.
-- L'application attribue ensuite automatiquement une référence aléatoire aux simulations existantes.
ALTER TABLE simulations
  ADD COLUMN IF NOT EXISTS reference_publique VARCHAR(36);

CREATE UNIQUE INDEX IF NOT EXISTS uk_simulations_reference_publique
  ON simulations (reference_publique);
