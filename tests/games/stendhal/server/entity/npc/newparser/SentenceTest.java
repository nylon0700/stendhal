package games.stendhal.server.entity.npc.newparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

/**
 * test ConversationParser class
 *
 * @author Martin Fuchs
 */
public class SentenceTest {

	@Test
	public final void testGrammar() {
		Sentence sentence = new Sentence();
		String text = ConversationParser.getSentenceType("The quick brown fox jumps over the lazy dog.", sentence);
		ConversationParser parser = new ConversationParser(text);
		sentence.parse(parser);
		sentence.classifyWords(parser);
		assertFalse(sentence.hasError());
		assertEquals("quick/ADJ brown/ADJ-COL fox/SUB-ANI jump/VER over/PRE lazy/ADJ dog/SUB-ANI .", sentence.toString());

		sentence.mergeWords();
		assertEquals("fox/SUB-ANI-COL jump/VER over/PRE dog/SUB-ANI .", sentence.toString());
		assertEquals(Sentence.ST_STATEMENT, sentence.getType());

		sentence = new Sentence();
		parser = new ConversationParser("does it fit");
		sentence.parse(parser);
		sentence.classifyWords(parser);
		assertFalse(sentence.hasError());
		assertEquals("do/VER it/OBJ fit/VER", sentence.toString());
		assertEquals(Sentence.ST_QUESTION, sentence.evaluateSentenceType());
		assertEquals("it/OBJ fit/VER ?", sentence.toString());
	}

	@Test
	public final void testSentenceType() {
		Sentence sentence = ConversationParser.parse("buy banana!");
		assertFalse(sentence.hasError());
		assertEquals(Sentence.ST_IMPERATIVE, sentence.getType());
		assertEquals("buy", sentence.getVerb(0).getNormalized());
		assertEquals("banana", sentence.getObject(0).getNormalized());

		sentence = ConversationParser.parse("do you have a banana for me?");
		assertFalse(sentence.hasError());
		assertEquals(Sentence.ST_QUESTION, sentence.getType());
		assertEquals("have", sentence.getVerb(0).getNormalized());
		assertEquals("banana", sentence.getObject(0).getNormalized());

		sentence = ConversationParser.parse("how are you?");
		assertFalse(sentence.hasError());
		assertEquals("is/VER-PLU-QUE you/SUB ?", sentence.toString());
		assertEquals(Sentence.ST_QUESTION, sentence.getType());

		sentence = ConversationParser.parse("this is a banana.");
		assertFalse(sentence.hasError());
		assertEquals("this/OBJ is/VER banana/OBJ-FOO .", sentence.toString());
		assertEquals(Sentence.ST_STATEMENT, sentence.getType());
		assertEquals("this", sentence.getObject(0).getNormalized());
		assertEquals("is", sentence.getVerb(0).getNormalized());
		assertEquals("banana", sentence.getObject(1).getNormalized());
	}

}
