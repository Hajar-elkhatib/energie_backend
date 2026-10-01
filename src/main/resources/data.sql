INSERT INTO regions (nom, lois, primes, champs_formulaire) VALUES
  ('Wallonie', 'Réglementation PEB wallonne et obligations déclaratives applicables (valeurs indicatives).', 'Primes Habitation Wallonie : montants variables selon revenus et audit préalable (indicatif).', 'typeLogement,anneeConstruction,consommationAnnuelle,revenueCategory'),
  ('Bruxelles', 'Réglementation Bruxelles Environnement / PEB applicable (valeurs indicatives).', 'Primes Renolution : aides variables selon catégorie de revenus et travaux (indicatif).', 'typeLogement,anneeConstruction,codePostal,consommationAnnuelle'),
  ('Flandre', 'EPB/VEKA : réglementation flamande applicable (valeurs indicatives).', 'Mijn VerbouwPremie : aides variables selon catégorie et revenus (indicatif).', 'typeLogement,anneeConstruction,energieLabel,consommationAnnuelle')
ON CONFLICT (nom) DO NOTHING;

-- Nettoyage limité aux doublons exactement identiques des produits de démonstration.
-- Les produits utilisateur avec une identité différente ne sont jamais supprimés.
WITH produits_dupliques AS (
  SELECT id, ROW_NUMBER() OVER (PARTITION BY nom, type, prix, specifications ORDER BY id) AS rang
  FROM produits
  WHERE nom IN (
    'Pack panneaux photovoltaïques 4 kWc',
    'Pack panneaux photovoltaïques 6 kWc',
    'Batterie résidentielle 10 kWh',
    'Pompe à chaleur air/eau',
    'Isolation toiture 100 m²'
  )
)
DELETE FROM produits p USING produits_dupliques d
WHERE p.id = d.id
  AND d.rang > 1
  AND NOT EXISTS (SELECT 1 FROM simulations s WHERE s.produit_id = p.id);

-- Insertion idempotente sans supprimer ni modifier les produits référencés.
INSERT INTO produits (nom, type, prix, specifications)
SELECT 'Pack panneaux photovoltaïques 4 kWc', 'panneaux', 6500.00, '10 panneaux monocristallins, onduleur inclus, puissance 4 kWc.'
WHERE NOT EXISTS (SELECT 1 FROM produits WHERE nom = 'Pack panneaux photovoltaïques 4 kWc' AND type = 'panneaux' AND prix = 6500.00 AND specifications = '10 panneaux monocristallins, onduleur inclus, puissance 4 kWc.');
INSERT INTO produits (nom, type, prix, specifications)
SELECT 'Pack panneaux photovoltaïques 6 kWc', 'panneaux', 8900.00, '15 panneaux monocristallins, onduleur inclus, puissance 6 kWc.'
WHERE NOT EXISTS (SELECT 1 FROM produits WHERE nom = 'Pack panneaux photovoltaïques 6 kWc' AND type = 'panneaux' AND prix = 8900.00 AND specifications = '15 panneaux monocristallins, onduleur inclus, puissance 6 kWc.');
INSERT INTO produits (nom, type, prix, specifications)
SELECT 'Batterie résidentielle 10 kWh', 'batterie', 7200.00, 'Batterie lithium 10 kWh, garantie 10 ans.'
WHERE NOT EXISTS (SELECT 1 FROM produits WHERE nom = 'Batterie résidentielle 10 kWh' AND type = 'batterie' AND prix = 7200.00 AND specifications = 'Batterie lithium 10 kWh, garantie 10 ans.');
INSERT INTO produits (nom, type, prix, specifications)
SELECT 'Pompe à chaleur air/eau', 'pompe à chaleur', 12500.00, 'Pompe à chaleur air/eau haute efficacité, installation comprise.'
WHERE NOT EXISTS (SELECT 1 FROM produits WHERE nom = 'Pompe à chaleur air/eau' AND type = 'pompe à chaleur' AND prix = 12500.00 AND specifications = 'Pompe à chaleur air/eau haute efficacité, installation comprise.');
INSERT INTO produits (nom, type, prix, specifications)
SELECT 'Isolation toiture 100 m²', 'isolation', 5400.00, 'Isolation laine minérale, résistance thermique conforme PEB.'
WHERE NOT EXISTS (SELECT 1 FROM produits WHERE nom = 'Isolation toiture 100 m²' AND type = 'isolation' AND prix = 5400.00 AND specifications = 'Isolation laine minérale, résistance thermique conforme PEB.');

-- Compte de développement migré au premier démarrage dev vers admin / admin123.
INSERT INTO administrateurs (login, mot_de_passe) VALUES
  ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy')
ON CONFLICT (login) DO NOTHING;
