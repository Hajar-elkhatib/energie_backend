INSERT INTO regions (nom, lois, primes, champs_formulaire) VALUES
  ('Wallonie', 'Réglementation PEB wallonne et obligations déclaratives applicables (valeurs indicatives).', 'Primes Habitation Wallonie : montants variables selon revenus et audit préalable (indicatif).', 'typeLogement,anneeConstruction,consommationAnnuelle,revenueCategory'),
  ('Bruxelles', 'Réglementation Bruxelles Environnement / PEB applicable (valeurs indicatives).', 'Primes Renolution : aides variables selon catégorie de revenus et travaux (indicatif).', 'typeLogement,anneeConstruction,codePostal,consommationAnnuelle'),
  ('Flandre', 'EPB/VEKA : réglementation flamande applicable (valeurs indicatives).', 'Mijn VerbouwPremie : aides variables selon catégorie et revenus (indicatif).', 'typeLogement,anneeConstruction,energieLabel,consommationAnnuelle')
ON CONFLICT (nom) DO NOTHING;

INSERT INTO produits (nom, type, prix, specifications) VALUES
  ('Pack panneaux photovoltaïques 4 kWc', 'panneaux', 6500.00, '10 panneaux monocristallins, onduleur inclus, puissance 4 kWc.'),
  ('Pack panneaux photovoltaïques 6 kWc', 'panneaux', 8900.00, '15 panneaux monocristallins, onduleur inclus, puissance 6 kWc.'),
  ('Batterie résidentielle 10 kWh', 'batterie', 7200.00, 'Batterie lithium 10 kWh, garantie 10 ans.'),
  ('Pompe à chaleur air/eau', 'pompe à chaleur', 12500.00, 'Pompe à chaleur air/eau haute efficacité, installation comprise.'),
  ('Isolation toiture 100 m²', 'isolation', 5400.00, 'Isolation laine minérale, résistance thermique conforme PEB.')
ON CONFLICT DO NOTHING;

-- Compte de développement : admin / password
INSERT INTO administrateurs (login, mot_de_passe) VALUES
  ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy')
ON CONFLICT (login) DO NOTHING;
