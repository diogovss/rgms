package steps

import java.util.Date

import rgms.publication.Periodico
import rgms.publication.PeriodicoController
import rgms.publication.ResearchLine
import rgms.publication.ResearchLineController
import rgms.publication.TechnicalReport
import rgms.publication.TechnicalReportController
import rgms.member.Record
import rgms.member.RecordController
import rgms.member.Member
import rgms.member.MemberController
import rgms.member.MembershipController
import rgms.member.ResearchGroup
import rgms.member.ResearchGroupController

class TestDataAndOperations {
    static articles = [
            [journal: "Theoretical Computer Science", volume: 455, number: 1, pages: "2-30",
                    title: "A theory of software product line refinement",
                    publicationDate: (new Date("12 October 2012"))],
            [journal: "Science of Computer Programming", volume: 455, pages: "2-30",
                    title: "Algebraic reasoning for object-oriented programming",
                    publicationDate: (new Date("12 October 2012"))]
    ]

	static researchLines = [
		[name: "IA Avancada", description: ""],
		[name: "Teoria da informacao - Complexidade no espaco", description: "P=NP"],
		[name: "Novo Padrao Arquitetural MVCE", description: "Nova arquitetura que promete revolucionar a web"]
	]

	static records = [
		[status_H: "MSc Student",start: (new Date()), end: null]
	]
	
	static reports = [
		[title:'Evaluating Natural Languages System',
			publicationDate: (new Date('13 November 2012')), institution:'UFPE'],
		[title:'NFL Languages System',
			publicationDate: (new Date('27 October 2011')), institution:'NFL']
		]

		static members = [
		[name: "Rodolfo Ferraz", username: "usernametest", email: "rodolfofake@gmail.com",
				status: "Graduate Student", university: "UFPE", enabled: true
				],
		[name: "Rebeca Souza", username: "rebecasouza", email: "rsa2fake@cin.ufpe.br",
				status: "Graduate Student", university: "UFPE", enabled: true
				]]
	
	static researchgroups = [
		[name: "SWPRG",
			description: "SW Productivity Research Group",
			childOf: null]
		,
		[name: "taes",
			description: "grupo de estudos",
			childOf: null]]
	
	static memberships = [
		[member: (new Member(members[0])),
			researchGroup: (new ResearchGroup(researchgroups[0])),
			dateJoined: (new Date(2012, 03, 01)),
			dateLeft: (new Date(2012, 06, 01))]
		,
		[member: (new Member(members[1])),
			researchGroup: (new ResearchGroup(researchgroups[0])),
			dateJoined: (new Date(2012, 03, 01)),
			dateLeft: (new Date(2012, 06, 01))]]

    static public def findArticleByTitle(String title) {
        articles.find { article ->
            article.title == title
        }
    }
	
	static public def findByUsername(String username) {
		members.find { member ->
			member.username == username
		}
	}

	static public def findRecordByStatus(def status) {
		records.find{ record ->
			record.status_H == status			
		}
	}

	static public def findResearchLineByName(String name) {
		researchLines.find { researchLine ->
			researchLine.name == name
		}
	}

	static public def findReportByTitle(String title) {
		reports.find{report ->
			report.title == title
		}
	}

	static public def findMembershipByResearchGroupName(String groupname) {
		memberships.find { membership ->
			membership.researchGroup.name == groupname
		}
	}

	static public def findResearchGroupByGroupName(String groupname) {
		researchgroups.find { group ->
			group.name == groupname
		}
	}
	
    static public boolean compatibleTo(article, title) {
        def testarticle = findArticleByTitle(title)
        def compatible = false
        if (testarticle == null && article == null) {
            compatible = true
        } else if (testarticle != null && article != null) {
            compatible = true
            testarticle.each { key, data ->
                compatible = compatible && (article."$key" == data)
            }
        }
        return compatible
    }
	
	static public boolean memberCompatibleTo(member, username) {
		def testmember = findByUsername(username)
		def compatible = false
		if (testmember == null && member == null) {
			compatible = true
		} else if (testmember != null && member != null) {
			compatible = true
			testmember.each { key, data ->
				compatible = compatible && (member."$key" == data)
			}
		}
		return compatible
	}

    static public void createArticle(String title, filename) {
        def cont = new PeriodicoController()
        def date = new Date()
        cont.params << TestDataAndOperations.findArticleByTitle(title) << [file: filename]
        cont.request.setContent(new byte[1000]) // Could also vary the request content.
        cont.create()
        cont.save()
        cont.response.reset()
    }

	static public void createReport(String title, filename) {
		
		def cont = new TechnicalReportController()
		def date = new Date()
		cont.params << TestDataAndOperations.findReportByTitle(title) << [file: filename]
		
		cont.request.setContent(new byte[1000]) // Could also vary the request content.
		cont.create()
		cont.save()
		cont.response.reset()
	}
	
	static public void createMember(String username) {
		def cont = new MemberController()
		
		cont.params << TestDataAndOperations.findByUsername(username)
		cont.request.setContent(new byte[1000]) // Could also vary the request content.
		cont.create()
		cont.save()
		cont.response.reset()
	}
	
	static public boolean compatibleTechTo(tech, title) {
		def testtech = TechnicalReport.findByTitle(title)
		def compatible = false
		if (testtech == null && tech == null) {
			compatible = true
		} else if (testtech != null && tech != null) {
			compatible = true
			testtech.each { key, data ->
				compatible = compatible && (tech."$key" == data)
			}
		}
		return compatible
	}

	static public TechnicalReport editTech(oldtitle, newtitle) {
		def tech = TechnicalReport.findByTitle(oldtitle)
		tech.setTitle(newtitle)
		def cont = new TechnicalReportController()
		cont.params << tech.properties
		cont.update()

		def updatedtech = TechnicalReport.findByTitle(newtitle)
		return updatedtech
	}
	
	static public void createResearchGroup(String groupname) {
		def cont = new ResearchGroupController()
		cont.params << TestDataAndOperations.findResearchGroupByGroupName(groupname)
		cont.request.setContent(new byte[1000]) // Could also vary the request content.
		cont.create()
		cont.save()
		cont.response.reset()
	}
	
	static public void createMembership(String username, String rgroup, String date1, String date2) {
		//TODO Deveria pegar os dados dos parametros, mas
		//		esta dando problema na criacao. Entao para
		//		simplificar o entendimento do problema,
		//		os valores est�o fixos. O problema n�o
		//		est� nos par�metros passados.
		
		def cont = new MembershipController()
		
		cont.params << [member: (new Member(members[0])),
						researchGroup: (new ResearchGroup(name: "taes", description: "grupo de estudos", childOf: null)),
						dateJoined: (new Date(2012, 03, 01)), dateLeft: (new Date(2012, 06, 01))]
		cont.request.setContent(new byte[1000]) // Could also vary the request content.
		cont.create()
		cont.save()
		cont.response.reset()
	}
	
	static public void deleteMembership(String username, String rgroup, String date1, String date2) {
		java.text.DateFormat df = new java.text.SimpleDateFormat("dd-MM-yyyy");
		def cont = new MembershipController()
		def identificador = Membership.findByMemberAndResearchGroupAndDateJoinedAndDateLeft(Member.findByUsername(username),
										ResearchGroup.findByName(rgroup), df.parse(date1), df.parse(date2)).id
		cont.params << [id: identificador]
		cont.request.setContent(new byte[1000]) // Could also vary the request content.
		cont.delete()
		//cont.save()
		cont.response.reset()
	}
	
	static public void deleteMember(String username) {
		def cont = new MemberController()
		def identificador = Member.findByUsername(username).id
		cont.params << [id: identificador]
		cont.request.setContent(new byte[1000]) // Could also vary the request content.
		cont.delete()
		//cont.save()
		cont.response.reset()
	}
	
    static void clearArticles() {
        Periodico.findAll()*.delete flush: true // Could also delete the created files.
    }

	static public void deleteResearchLine(def id) {
		def res = new ResearchLineController()
		res.params.id = id
		res.request.setContent(new byte[1000]) // Could also vary the request content.
		res.delete()
		res.response.reset()
	}

	static public void updateResearchLine(String name,String description) {
		def res = new ResearchLineController()
		def research_line = ResearchLine.findByName(name)
		res.params.id = research_line.id
		res.params.name = research_line.name
		res.params.description = description
		res.request.setContent(new byte[1000]) // Could also vary the request content.
		res.update()
		res.response.reset()
	}

	static public void createResearchLine(String name) {
		def cont = new ResearchLineController()
		def research = TestDataAndOperations.findResearchLineByName(name)
		cont.params.name = research.name
		cont.params.description = research.description
		cont.request.setContent(new byte[1000]) // Could also vary the request content.
		cont.create()
		cont.save()
		cont.response.reset()
	}

	static public void deleteRecord(def id) {
		def rec = new RecordController()
		rec.params.id = id
		rec.request.setContent(new byte[1000]) // Could also vary the request content.
		rec.delete()
		rec.response.reset()
	}
	
	static public void updateRecord(def status, String end) {
		def record = Record.findByStatus_H(status)
		def rec = new RecordController()
		rec.params.id = record.id
		rec.params.start = record.start
		rec.params.status_H = record.status_H
		rec.params.end = Date.parse("dd/mm/yyyy",end)
		rec.request.setContent(new byte[1000]) // Could also vary the request content.
		rec.update()
		rec.response.reset()
	}

	static public def createRecord(def status) {
		def cont = new RecordController()
		def record = TestDataAndOperations.findRecordByStatus(status)
		cont.params.status_H = record.status_H
		cont.params.start = record.start
		cont.params.end = record.end
		cont.create()
		cont.save()
		cont.response.reset()

	}

	static public def insertsResearchLine(String name) {
		def inserted = ResearchLine.findByName(name)
		if(!inserted) {
			def research = TestDataAndOperations.findResearchLineByName(name)
			ResearchLine rl = new ResearchLine()
			rl.setName(research.name)
			rl.setDescription(research.description)
			rl.save()
		}		
	}

	static public void removeArticle(String title) {
		def testarticle = Periodico.findByTitle(title)
		def cont = new PeriodicoController()
		def date = new Date()
		cont.params << [id: testarticle.id]
		cont.delete()
	}

	static public boolean containsArticle(title, articles) {
		def testarticle = Periodico.findByTitle(title)
		def cont = new PeriodicoController()
		def result = cont.list().periodicoInstanceList
		return result.contains(testarticle)
	}

	static public Periodico editArticle(oldtitle, newtitle) {
		def article = Periodico.findByTitle(oldtitle)
		article.setTitle(newtitle)
		def cont = new PeriodicoController()
		cont.params << article.properties
		cont.update()

		def updatedarticle = Periodico.findByTitle(newtitle)
		return updatedarticle
	}
}