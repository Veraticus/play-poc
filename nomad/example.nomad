job "play-poc" {
  datacenters = ["dc1"]

  update {
    stagger = "10s"
    max_parallel = 1
  }

  group "web" {
    count = 3

    task "play-poc-app" {
      driver = "docker"

      env {
        redeploy = "YES"
      }

      config {
        image = "play-poc:1.0-SNAPSHOT"
        port_map {
          http = 9000
          cluster = 2552
        }
      }

      service {
        tags = ["urlprefix-/"]

        name = "http"
        port = "http"

        check {
          type = "http"
          path = "/health"
          interval = "10s"
          timeout = "2s"
        }
      }

      service {
        name = "discovery"
        port = "cluster"
      }

      service {
        name = "cluster"
        port = "cluster"
      }

      resources {
        cpu    = 500
        memory = 128
        network {
          mbits = 5
          port "cluster" { }
          port "http" { }
        }
      }
    }
  }
}
