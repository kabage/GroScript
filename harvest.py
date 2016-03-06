
Conversation opened. 2 messages. All messages read.

Skip to content
Using Gmail with screen readers
edward
Click here to enable desktop notifications for Gmail.   Learn more  Hide


More
8 of 1,837

Gro Hackathon Starter Script
Inbox
	x
Victor Kipkoech <victor.kipkoech@gro-intelligence.com>

AttachmentsMar 5 (1 day ago)

to adriateri, alexk249076, amosnzivu, Binmonk, brndnyokabi, dai.tensai1, denzjoseph, ericmwenda5, gaithoben, gitonga.mwenda, imadege1990, info, me, kiilueric, kingkonig, mainad57, martindeto, morris, nyongesa.pius2., olooesqr, peterd.mureithi, savioabuga, tommwenda, vivianmondi, Hezron
Hi!

To help get you started, here's a simple starter script for harvest.py file. It meets the API requirements we defined for you here. Feel free to edit it.

To test run:
python harvest.py

For help, run
python harvest.py -h

Example call:
python harvest.py  --database_host arg1 --database_name arg2 --start_date=2005-1-1

Kind regards
2 Attachments
Preview attachment harvest.py
[Text]
Preview attachment March 5 Gro Hackathon - Problem Definition
[Google Docs]
Hezron Obuchele <hezron.obuchele@gro-intelligence.com>

AttachmentsMar 5 (1 day ago)

to Victor, adriateri, alexk249076, amosnzivu, Binmonk, brndnyokabi, dai.tensai1, denzjoseph, ericmwenda5, gaithoben, gitonga.mwenda, imadege1990, info, me, kiilueric, kingkonig, mainad57, martindeto, morris, nyongesa.pius2., olooesqr, peterd.mureithi, savioabuga, tommwenda, vivianmondi
Here's the problem definition for those who didn't get the Google doc link.
Attachments area
Preview attachment March5GroHackathon-ProblemDefinition.pdf
[PDF]

Click here to Reply, Reply to all, or Forward
1.53 GB (10%) of 15 GB used
Manage
Terms - Privacy
Last account activity: 0 minutes ago
Details


25 more
	Hezron Obuchele
Show details

import sys
import getopt


def begin_nass_harvest(database_host, database_name, database_user, database_password,
                       port, start_date, end_date):
    print "\nThis is a starter script for the Gro Hackathon's NASS harvest. It meets the API " \
          "requirements defined for the hackathon\n\n"

    print "Run 'python harvest.py -h' for help\n\n"
    print "Feel free to edit the entirety of this start script\n"

    print "Supplied Args (some default): "
    print "Database Host: {}".format(database_host)
    print "Database Name: {}".format(database_name)
    print "Database Username: {}".format(database_user)
    print "Database Password: {}".format(database_password)
    print "Database Port (hard-coded): {}".format(port)
    print "Harvest Start Date: {}".format(start_date)
    print "Harvest End Date: {}\n".format(end_date)

    
# #################################################
# PUT YOUR CODE ABOVE THIS LINE
# #################################################
def main(argv):
    try:
        opts, args = getopt.getopt(argv, "h", ["database_host=", "database_name=", "start_date=",
                                               "database_user=", "database_pass=", "end_date="])
    except getopt.GetoptError:
        print 'Flag error. Probably a mis-typed flag. Make sure they start with "--". Run python ' \
              'harvest.py -h'
        sys.exit(2)

    #define defaults
    database_host = 'localhost'
    database_name = 'gro'
    port = 5432
    database_user = 'gro'
    database_password = 'gro123'
    start_date = '2005-1-1'
    end_date = '2015-12-31'

    for opt, arg in opts:
        if opt == '-h':
            print "\nThis is my harvest script for the Gro Hackathon NASS harvest"
            print '\nExample:\npython harvest.py --database_host localhost --database_name gro2\n'
            print '\nFlags (all optional, see defaults below):\n ' \
              '--database_host [default is "{}"]\n ' \
              '--database_name [default is "{}"]\n ' \
              '--database_user [default is "{}"]\n ' \
              '--database_pass [default is "{}"]\n ' \
              '--start_date [default is "{}"]\n ' \
              '--end_date [default is "{}"]\n'.format(database_host, database_name, database_user,
                                                      database_password, start_date, end_date)
            sys.exit()
        elif opt in ("--database_host"):
            database_host = arg
        elif opt in ("--database_name"):
            database_name = arg
        elif opt in ("--database_user"):
            database_user = arg
        elif opt in ("--database_pass"):
            database_password = arg
        elif opt in ("--start_date"):
            start_date = arg
        elif opt in ("--end_date"):
            end_date = arg

    begin_nass_harvest(database_host, database_name, database_user, database_password,
                       port, start_date, end_date)

if __name__ == "__main__":
   main(sys.argv[1:])

harvest.py
Displaying harvest.py.
