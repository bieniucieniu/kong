{ pkgs, ... }:
{
  packages = [ pkgs.git ];

  env = {
    DATABASE_URL = "postgresql://kong:kong@localhost:5432/kong";
  };

  languages = {
    javascript = {
      enable = true;
      directory = "./services/frontend/";
      bun = {
        enable = true;
        install.enable = true;
      };
    };

    typescript = {
      enable = true;
    };
  };

  services.postgres = {
    enable = true;
    listen_addresses = "localhost";
    initialScript = ''
      CREATE ROLE kong WITH LOGIN SUPERUSER;
    '';
    initialDatabases = [
      {
        name = "kong";
        user = "kong";
      }
    ];
  };

  processes = {
    frontend.exec = "cd frontend/ && bun run dev";
    # sortybooks.exec = "cd services/frontend/ && bun run storybook";
  };
}