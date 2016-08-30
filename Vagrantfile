# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure("2") do |config|
  config.vm.box = "ubuntu/trusty64"
  config.ssh.forward_agent = true
  config.vm.network :forwarded_port, guest: 6379, host: 6379
  config.vm.network :forwarded_port, guest: 5432, host: 5432

  config.vm.provider :virtualbox do |v|
    v.memory = 1024
    v.cpus = 1 
    # Various improvements to internet speed quality: https://github.com/mitchellh/vagrant/issues/1807
    v.customize ["modifyvm", :id, "--nictype1", "virtio"]  
    v.customize ["modifyvm", :id, "--natdnshostresolver1", "on"]
    v.customize ["modifyvm", :id, "--natdnsproxy1", "on"]
  end

  config.vm.provision :ansible do |ansible|
    ansible.playbook = "./environment-setup/infrastructure.yml"
    ansible.sudo = true
    #ansible.verbose = true
  end
end

