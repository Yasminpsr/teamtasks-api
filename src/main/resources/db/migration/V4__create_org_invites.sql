CREATE TABLE org_invites (
  id UUID PRIMARY KEY,
  organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
  email VARCHAR(150) NOT NULL,
  role VARCHAR(20) NOT NULL,
  token_hash VARCHAR(255) NOT NULL,
  expires_at TIMESTAMP NOT NULL,
  accepted_at TIMESTAMP NULL,
  revoked_at TIMESTAMP NULL,
  created_by UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_org_invites_org_id ON org_invites(organization_id);
CREATE INDEX idx_org_invites_email ON org_invites(email);
CREATE INDEX idx_org_invites_token_hash ON org_invites(token_hash);

CREATE UNIQUE INDEX uq_org_invites_active
ON org_invites (organization_id, email)
WHERE accepted_at IS NULL AND revoked_at IS NULL;