require 'pg'

begin
  ddl = File.open('crowdsig_ddl.sql', 'r') { |f| f.read }
  con = PG.connect :dbname => 'testdb', :user => 'crowdsig', :password => 'crowdsig'
  con.exec ddl
rescue PG::Error => e
  puts e.message 
ensure
  con.close if con
end
