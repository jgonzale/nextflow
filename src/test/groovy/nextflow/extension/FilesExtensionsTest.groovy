package nextflow.extension
import java.nio.file.Files
import java.nio.file.Paths

import spock.lang.Specification
/**
 *
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
class FilesExtensionsTest extends Specification {



    def testGetExtension() {

        expect:
        new File('file.txt').getExtension() == 'txt'
        Paths.get('file.txt').getExtension() == 'txt'
        new File('file_name').getExtension() == ''
        Paths.get('file_name').getExtension() == ''

    }

    def testBaseName() {

        expect:
        new File('/path/file.txt').getBaseName() == 'file'
        Paths.get('/path/file.txt').getBaseName() == 'file'

        new File('/path/file_name').getBaseName() == 'file_name'
        Paths.get('/path/file_name').getBaseName() == 'file_name'

        new File('/path/').getBaseName() == 'path'
        Paths.get('/path/').getBaseName() == 'path'

        new File('/').getBaseName() == ''
        Paths.get('/').getBaseName() == ''

    }


    def testGetName() {

        expect:
        new File('/path/file.txt').getName() == 'file.txt'
        Paths.get('/path/file.txt').getName() == 'file.txt'

        new File('/path/file_name').getName() == 'file_name'
        Paths.get('/path/file_name').getName() == 'file_name'

        new File('/path/').getName() == 'path'
        Paths.get('/path/').getName() == 'path'

        new File('/').getName() == ''
        Paths.get('/').getName() == ''

    }

    def testPathMakeDir() {

        when:
        def path = Paths.get('xyz')
        then:
        path.mkdir()

        cleanup:
        path.deleteDir()


    }


    def 'test baseName' () {

        expect:
        new File('a/b/file.text').baseName == 'file'
        new File('.').baseName == ''

    }

    def 'test file extension' () {

        expect:
        new File('a/b/file.txt').extension == 'txt'
        new File('.').baseName == ''

    }

    def 'test copyTo' () {

        setup:
        def source = File.createTempFile('test','source')
        source.text = 'Hello'

        when:
        def copy = source.copyTo( File.createTempFile('test','target') )
        then:
        copy.text == 'Hello'
        source.exists()

        when:
        def folder = Files.createTempDirectory('testCopyTo').toFile()
        def copy2 = source.copyTo(folder)
        then:
        copy2.text == 'Hello'
        copy2.name == source.name
        source.exists()

        cleanup:
        source?.delete()
        copy?.delete()
        folder?.deleteDir()
    }

    def 'test copy source directory (file) to target directory' () {

        when:
        def sourceFolder =  Files.createTempDirectory('folderToCopy').toFile()
        def path1 = new File(sourceFolder, 'file1')
        def path2 = new File(sourceFolder, 'file2')
        def path3 = new File(sourceFolder, 'file3')
        path1.text = 'Hello1'
        path2.text = 'Hello2'
        path3.text = 'Hello3'
        def target = Files.createTempDirectory('targetFolder').resolve('xxx').toFile()
        // try to copy the source folder to the target
        sourceFolder.copyTo(target)

        then:
        new File(target, 'file1').text == 'Hello1'
        new File(target, 'file2').text == 'Hello2'
        new File(target, 'file3').text == 'Hello3'
        !new File(target, 'file4').exists()

        cleanup:
        sourceFolder?.deleteDir()
        target?.deleteDir()

    }


    def 'test copyTo path' () {

        setup:
        def source = Files.createTempFile('test','source')
        source.text = 'Hello'

        when:
        def copy = source.copyTo( Files.createTempFile('test','target') )
        then:
        copy.text == 'Hello'
        source.exists()

        when:
        def folder = Files.createTempDirectory('testCopyTo')
        def copy2 = source.copyTo(folder)
        then:
        copy2.text == 'Hello'
        copy2.name == source.name
        source.exists()

        cleanup:
        source?.delete()
        copy?.delete()
        folder?.deleteDir()
    }

    def 'test copy source directory (path) to another directory' () {

        when:
        def sourceFolder =  Files.createTempDirectory('folderToCopy')
        def path1 = sourceFolder.resolve('file1')
        def path2 = sourceFolder.resolve('file2')
        def path3 = sourceFolder.resolve('file3')
        def sub1 = Files.createTempDirectory(sourceFolder, 'sub1')
        def path4 = sub1.resolve('file4')
        path1.text = 'Hello1'
        path2.text = 'Hello2'
        path3.text = 'Hello3'
        path4.text = 'Hello4'
        def targetFolder = Files.createTempDirectory('targetFolder')
        // try to copy the source folder to the target
        sourceFolder.copyTo(targetFolder)
        then:
        targetFolder.resolve('file1').text == 'Hello1'
        targetFolder.resolve('file2').text == 'Hello2'
        targetFolder.resolve('file3').text == 'Hello3'
        targetFolder.resolve(sub1.getName()).resolve('file4').text == 'Hello4'

        cleanup:
        sourceFolder.deleteDir()
        targetFolder.deleteDir()

    }

    def 'test moveTo' () {

        setup:
        // source1
        def source1 = Files.createTempFile('test','source')
        source1.text = 'Hello 1'


        /*
         * test moving to a file with a different name
         */
        when:
        def file1 = source1.moveTo( Files.createTempFile('targetFile',null) )
        then:
        !source1.exists()
        file1.text == 'Hello 1'

        /*
         * test moving to a FOLDER
         */
        when:
        def folder = Files.createTempDirectory('testMoveTo')
        def source2 = Files.createTempFile('test','source')
        source2.text = 'Hello 2'
        def file2 = source2.moveTo(folder)
        then:
        file2.text == 'Hello 2'
        file2.name == source2.name
        !source2.exists()


        cleanup:
        file1?.delete()
        file2?.delete()
        folder?.deleteDir()

    }

    def 'test moveTo (directory) ' () {

        given:
        def sourceFolder =  Files.createTempDirectory('folderToCopy')
        def path1 = sourceFolder.resolve('file1')
        def path2 = sourceFolder.resolve('file2')
        def path3 = sourceFolder.resolve('file3')
        def sub1 = Files.createTempDirectory(sourceFolder, 'sub1')
        def path4 = sub1.resolve('file4')
        path1.text = 'Hello1'
        path2.text = 'Hello2'
        path3.text = 'Hello3'
        path4.text = 'Hello4'
        def targetFolder = Files.createTempDirectory('targetFolder')

        when:
        targetFolder = sourceFolder.moveTo(targetFolder)
        then:
        !sourceFolder.exists()
        targetFolder.getName() == sourceFolder.getName()
        targetFolder.resolve('file1').text == 'Hello1'
        targetFolder.resolve('file2').text == 'Hello2'
        targetFolder.resolve('file3').text == 'Hello3'
        targetFolder.resolve(sub1.getName()).resolve('file4').text == 'Hello4'

        cleanup:
        sourceFolder?.deleteDir()
        targetFolder.getParent()?.deleteDir()


    }

    def testFileShit() {

        setup:
        def file = new File('test_file_shift')
        def path = Paths.get('test_path_shift')

        when:
        file << 'hola'
        path << 'hello'

        then:
        file.text == 'hola'
        path.text == 'hello'

        cleanup:
        file.delete()
        path.delete()

    }

    def testFileTail() {

        setup:
        def file = File.createTempFile('tailtest','txt')
        file.deleteOnExit()

        when:
        file.text = '''
        1
        22
        333
        4444
        55555
        666666
        7777777
        88888888
        999999999
        '''.stripIndent()

        then:
        file.tail(1).toString() == '999999999'
        file.tail(2).toString() == '88888888\n999999999'
        file.tail(2,null,8).toString() == '88888888\n999999999'
        file.tail(2,null,9).toString() == '88888888\n999999999'
        file.tail(2,null,10).toString() == '88888888\n999999999'
        file.tail(2,null,11).toString() == '88888888\n999999999'
        file.tail(2,null,12).toString() == '88888888\n999999999'

        when:
        def file2 = File.createTempFile('tailtest','txt')
        file2.deleteOnExit()
        file2.text = '''
        1
        22
        333
        4444
        55555
        666666
        7777777


        88888888

        999999999'''.stripIndent()

        then:
        file2.tail(1).toString() == '999999999'
        file2.tail(3).toString() == '88888888\n\n999999999'
        file2.tail(6).toString() == '7777777\n\n\n88888888\n\n999999999'
        file2.tail(6,null,8).toString() == '7777777\n\n\n88888888\n\n999999999'
        file2.tail(6,null,9).toString() == '7777777\n\n\n88888888\n\n999999999'
        file2.tail(6,null,10).toString() == '7777777\n\n\n88888888\n\n999999999'
        file2.tail(6,null,11).toString() == '7777777\n\n\n88888888\n\n999999999'
        file2.tail(6,null,12).toString() == '7777777\n\n\n88888888\n\n999999999'

    }


    def testFileHead() {

        setup:
        def file = File.createTempFile('tailtest','txt')
        file.deleteOnExit()

        when:
        file.text = '''\
        1
        22
        333


        4444
        55555
        666666
        7777777
        88888888
        999999999
        '''.stripIndent()

        then:
        file.head(1).toString() == '1'
        file.head(2).toString() == '1\n22'
        file.head(6).toString() == '1\n22\n333\n\n\n4444'

    }

    def testFileList() {

        given:
        def sourceFolder =  Files.createTempDirectory('folderToCopy')
        def path1 = sourceFolder.resolve('file1')
        def path2 = sourceFolder.resolve('file2')
        def path3 = sourceFolder.resolve('file3')
        def sub1 = Files.createTempDirectory(sourceFolder, 'sub1')
        def path4 = sub1.resolve('file4')
        def path5 = sub1.resolve('file5')
        path1.text = 'Hello1'
        path2.text = 'Hello2'
        path3.text = 'Hello3'
        path4.text = 'Hello4'
        path5.text = 'Hello5'

        when:
        def files = sourceFolder.toFile().listFiles()
        def paths = sourceFolder.listFiles()

        then:
        files.size() == 4
        files[0] == new File(sourceFolder.toString(), 'file1')
        files[1] == new File(sourceFolder.toString(), 'file2')
        files[2] == new File(sourceFolder.toString(), 'file3')
        files[3] == sub1.toFile()

        paths.size() == 4
        paths[0] == path1
        paths[1] == path2
        paths[2] == path3
        paths[3] == sub1

        expect:
        path1.toFile().listFiles() == null
        path1.listFiles() == null


        cleanup:
        sourceFolder.deleteDir()
    }


    def testSetReadonly() {

        setup:
        def file1 = File.createTempFile('testfile',null)
        def file2 = File.createTempFile('testfile',null)
        file1.deleteOnExit()
        file2.deleteOnExit()

        when:
        file1.setReadOnly()
        def OK = file2.toPath().setReadOnly()

        then:
        OK
        file1.toPath().getPermissions() == file2.toPath().getPermissions()

        expect:
        !Paths.get('none').setReadOnly()
    }

    def testSetExecutable () {
        setup:
        def file1 = File.createTempFile('testfile',null)
        def file2 = File.createTempFile('testfile',null)
        file1.deleteOnExit()
        file2.deleteOnExit()

        when:
        file1.setExecutable(true,false)
        def OK = file2.toPath().setExecutable(true,false)
        println "file1: ${file1.toPath().getPermissions()}"
        println "file2: ${file2.toPath().getPermissions()}"
        then:
        OK
        file1.toPath().getPermissions() == file2.toPath().getPermissions()

        when:
        file1.setExecutable(true)
        file2.toPath().setExecutable(true)
        println "file1: ${file1.toPath().getPermissions()}"
        println "file2: ${file2.toPath().getPermissions()}"
        then:
        file1.toPath().getPermissions() == file2.toPath().getPermissions()

        when:
        file1.setExecutable(false)
        file2.toPath().setExecutable(false)
        println "file1: ${file1.toPath().getPermissions()}"
        println "file2: ${file2.toPath().getPermissions()}"
        then:
        file1.toPath().getPermissions() == file2.toPath().getPermissions()

        when:
        file1.setExecutable(false,false)
        file2.toPath().setExecutable(false,false)
        println "file1: ${file1.toPath().getPermissions()}"
        println "file2: ${file2.toPath().getPermissions()}"
        then:
        file1.toPath().getPermissions() == file2.toPath().getPermissions()

        expect:
        !Paths.get('none').setExecutable(true)

    }

    def testSetWritable() {
        setup:
        def file1 = File.createTempFile('testfile',null)
        def file2 = File.createTempFile('testfile',null)
        file1.deleteOnExit()
        file2.deleteOnExit()

        when:
        file1.setWritable(true,false)
        def OK = file2.toPath().setWritable(true,false)
        println "file1: ${file1.toPath().getPermissions()}"
        println "file2: ${file2.toPath().getPermissions()}"
        then:
        OK
        file1.toPath().getPermissions() == file2.toPath().getPermissions()

        when:
        file1.setWritable(true)
        file2.toPath().setWritable(true)
        println "file1: ${file1.toPath().getPermissions()}"
        println "file2: ${file2.toPath().getPermissions()}"
        then:
        file1.toPath().getPermissions() == file2.toPath().getPermissions()

        when:
        file1.setWritable(false)
        file2.toPath().setWritable(false)
        println "file1: ${file1.toPath().getPermissions()}"
        println "file2: ${file2.toPath().getPermissions()}"
        then:
        file1.toPath().getPermissions() == file2.toPath().getPermissions()

        when:
        file1.setWritable(false,false)
        file2.toPath().setWritable(false,false)
        println "file1: ${file1.toPath().getPermissions()}"
        println "file2: ${file2.toPath().getPermissions()}"
        then:
        file1.toPath().getPermissions() == file2.toPath().getPermissions()

        expect:
        !Paths.get('none').setWritable(true)


    }

    def testSetReadable() {
        setup:
        def file1 = File.createTempFile('testfile',null)
        def file2 = File.createTempFile('testfile',null)
        file1.deleteOnExit()
        file2.deleteOnExit()

        when:
        file1.setReadable(true,false)
        def OK = file2.toPath().setReadable(true,false)
        println "file1: ${file1.toPath().getPermissions()}"
        println "file2: ${file2.toPath().getPermissions()}"
        then:
        OK
        file1.toPath().getPermissions() == file2.toPath().getPermissions()

        when:
        file1.setReadable(true)
        file2.toPath().setReadable(true)
        println "file1: ${file1.toPath().getPermissions()}"
        println "file2: ${file2.toPath().getPermissions()}"
        then:
        file1.toPath().getPermissions() == file2.toPath().getPermissions()

        when:
        file1.setReadable(false)
        file2.toPath().setReadable(false)
        println "file1: ${file1.toPath().getPermissions()}"
        println "file2: ${file2.toPath().getPermissions()}"
        then:
        file1.toPath().getPermissions() == file2.toPath().getPermissions()

        when:
        file1.setReadable(false,false)
        file2.toPath().setReadable(false,false)
        println "file1: ${file1.toPath().getPermissions()}"
        println "file2: ${file2.toPath().getPermissions()}"
        then:
        file1.toPath().getPermissions() == file2.toPath().getPermissions()


        expect:
        !Paths.get('none').setReadable(true)

    }

    def testDigitToPerm() {

        expect:
        FilesExtensions.digitToPerm(1).toString() == '--x'
        FilesExtensions.digitToPerm(2).toString() == '-w-'
        FilesExtensions.digitToPerm(3).toString() == '-wx'
        FilesExtensions.digitToPerm(4).toString() == 'r--'
        FilesExtensions.digitToPerm(5).toString() == 'r-x'
        FilesExtensions.digitToPerm(6).toString() == 'rw-'
        FilesExtensions.digitToPerm(7).toString() == 'rwx'

    }

    def testSetPermission() {
        setup:
        def file1 = File.createTempFile('testfile',null)
        file1.deleteOnExit()

        when:
        file1.setReadable(true,false)
        def ok = file1.toPath().setPermissions(6,4,4)
        then:
        ok
        file1.toPath().getPermissions() == 'rw-r--r--'

        expect:
        !Paths.get('none').setPermissions(6,6,6)

    }

    def testSetPermissionString() {
        setup:
        def file1 = File.createTempFile('testfile',null)
        file1.deleteOnExit()

        when:
        file1.setReadable(true,false)
        def ok = file1.toPath().setPermissions('rw-rw-rw-')
        then:
        ok
        file1.toPath().getPermissions() == 'rw-rw-rw-'

        expect:
        !Paths.get('none').setPermissions('rw-rw-rw-')

    }

}
