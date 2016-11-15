`export DOCKER_HOST=tcp://localhost:2375`
`export NOMAD_ADDR=http://play-poc.m1finance.vagrant:4646`

# -*- mode: ruby -*-
# vi: set ft=ruby :

$script = <<SCRIPT
# Restart Docker to get new defaults
sudo cp /tmp/etc_default_docker /etc/default/docker
sudo service docker restart

# Update apt and get dependencies
sudo apt-get update
sudo apt-get install -y unzip curl wget vim bind9

# Place bind config
sudo cp /tmp/etc_bind_named_conf_options /etc/bind/named.conf.options
sudo cp /tmp/etc_bind_consul_conf /etc/bind/consul.conf
sudo cp /tmp/etc_resolv_conf /etc/resolv.conf
sudo service bind9 restart

# Download Consul
echo Fetching Consul...
cd /tmp/
curl https://releases.hashicorp.com/consul/0.7.1/consul_0.7.1_linux_amd64.zip -o consul.zip

echo Installing Consul...
unzip consul.zip
sudo chmod +x consul
sudo mv consul /usr/bin/consul

sudo mkdir /etc/consul.d
sudo chmod a+w /etc/consul.d

pgrep consul || sudo nohup /usr/bin/consul agent -dev -ui >/var/log/consul.log 2>&1 &

# Download Nomad
echo Fetching Nomad...
cd /tmp/
curl -sSL https://releases.hashicorp.com/nomad/0.5.0-rc2/nomad_0.5.0-rc2_linux_amd64.zip -o nomad.zip

echo Installing Nomad...
unzip nomad.zip
sudo chmod +x nomad
sudo mv nomad /usr/bin/nomad

sudo mkdir -p /etc/nomad.d
sudo chmod a+w /etc/nomad.d

pgrep nomad || sudo nohup /usr/bin/nomad agent -dev >/var/log/nomad.log 2>&1 &

# Download Nomad UI
echo Fetching Nomad UI...
cd /tmp/
curl -sSL https://github.com/iverberk/nomad-ui/releases/download/v0.3.1/nomad-ui-linux-amd64 -o nomad-ui
sudo chmod +x nomad-ui
sudo mv nomad-ui /usr/bin/nomad-ui

pgrep nomad-ui || sudo nohup /usr/bin/nomad-ui >/var/log/nomad-ui.log 2>&1 &

# Install Fabio
echo Fetching Fabio...
cd /tmp/
curl -sSL https://github.com/eBay/fabio/releases/download/v1.3.4/fabio-1.3.4-go1.7.3-linux_amd64 -o fabio
sudo chmod +x fabio
sudo mv fabio /usr/bin/fabio

pgrep fabio || sudo nohup /usr/bin/fabio>/var/log/fabio.log 2>&1 &
SCRIPT

Vagrant.configure(2) do |config|
  config.vm.box = "ubuntu/trusty64"
  config.vm.hostname = 'play-poc'
  config.vm.provision "docker"
  config.vm.provision "file", source: "./vagrant/docker.default", destination: "/tmp/etc_default_docker"
  config.vm.provision "file", source: "./vagrant/named.conf.options", destination: "/tmp/etc_bind_named_conf_options"
  config.vm.provision "file", source: "./vagrant/consul.conf", destination: "/tmp/etc_bind_consul_conf"
  config.vm.provision "file", source: "./vagrant/resolv.conf", destination: "/tmp/etc_resolv_conf"
  config.vm.provision "shell", inline: $script

  config.vm.network "forwarded_port", guest: 2375, host: 2375
  config.vm.network "forwarded_port", guest: 4646, host: 4646
  config.vm.network "forwarded_port", guest: 3000, host: 3000
  config.vm.network "forwarded_port", guest: 9999, host: 9999
  config.vm.network "forwarded_port", guest: 8500, host: 8500

  config.vm.synced_folder ".", "/mnt"

  config.vm.provider "virtualbox" do |vb|
    vb.memory = "2048"
  end
end
