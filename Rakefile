# Add your own tasks in files placed in lib/tasks ending in .rake,
# for example lib/tasks/capistrano.rake, and they will automatically be available to Rake.

require 'rake'
require 'rake/clean'
require 'rake/testtask'
require 'rake/rdoctask'
require 'fileutils'


CLEAN.include("bin/**/*")

#TODO: Get jruby-core 
lib_path = "lib"

desc "Compile the Java source into class files"
#I've a feeling that this won't be DRY very quickly.  We should work on making it so.
task "javac" do |t|
  #compiling all the files (ultimately want to check file times to build only the ones we are interested in?)
  puts "Compiling Util" if RakeFileUtils.verbose_flag
  dest_path = "bin"
  Dir.mkdir "#{dest_path}" unless File.exist? "#{dest_path}"
  
  build_path = ["src/java"]
  
  files = build_path.collect do |path|
    Dir.glob("#{path}/**/*.java")
  end
    
  dependencies = ["wrapper.jar", "jruby-complete.jar", "args4j-2.0.9.jar", "junit-4.4.jar"]
  cp = Array.new
  dependencies.each do |dependency|
    cp << "#{lib_path}/#{dependency}"
  end
  puts "javac -classpath \"#{cp.join(File::PATH_SEPARATOR)}\" -sourcepath \"#{build_path.join(File::PATH_SEPARATOR)}\" -d #{dest_path} #{files.join(' ')}" if RakeFileUtils.verbose_flag
  
  `javac -classpath "#{cp.join(File::PATH_SEPARATOR)}" -sourcepath "#{build_path.join(File::PATH_SEPARATOR)}" -d #{dest_path} #{files.join(' ')}`
  unless $?.exitstatus == 0
    puts "Build failed on #{t.name}, see above errors."
    exit -1
  end
end

desc "Create utilities jar file"
task "jar" => "javac" do |t|
  puts "Building util.jar" if RakeFileUtils.verbose_flag
  classes = "bin"
  #TODO: handle versioning and handle creating a manifest from a manifest file.
  jarname = "util"
  `jar cf #{classes}/#{jarname}.jar -C #{classes} .`
  unless $?.exitstatus == 0
    puts "Build failed on #{t.name}"
    exit -1
  end
end

desc "Copy the jar and ruby files to the bin folder"
#TODO: get us to pass the jarname from the previous task (the dependent one.)  FIXME to work
task "deploy" => "jar" do |t|
  puts "copying jar and ruby files to #{lib_path}"  if RakeFileUtils.verbose_flag
  jarname = "util.jar"
  dest_name = "#{lib_path}"
  deploy_ruby = true
  Dir.mkdir "#{dest_name}/ruby" unless File.exist? "#{dest_name}/ruby"
  Dir.mkdir "#{dest_name}/ruby/service" unless File.exist? "#{dest_name}/ruby/service"
  FileUtils.cp_r "src/ruby/.", "#{dest_name}/ruby/service", :remove_destination => true
  FileUtils.cp "bin/util.jar", "#{dest_name}/util.jar"
end