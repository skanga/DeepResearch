
## How the DeepResearch tool works 

1. User query → LLM prompt – You type a research question. The front‑end wraps it in a structured prompt
   that tells the LLM to act as a researcher: outline sub‑questions, decide what facts need fresh web data,
   and format a plan.

2. LLM generates a research plan – The model breaks the topic into logical sections (e.g., “battery
   chemistries”, “thermal storage”, “grid‑scale case studies”) and decides which sections require a live
   search (usually the latest data, statistics, or recent papers).

3. Search request → DuckDuckGo API – For each “search‑needed” sub‑question the tool issues a DuckDuckGo
   query (via its instant‑answer or HTML endpoint). The raw result snippets (titles, URLs, short extracts)
   are returned to the LLM.

4. LLM consumes search results – The model receives the snippets, extracts the most relevant facts, and
   integrates them into the draft answer. It may also request additional pages if the first results are
   insufficient, looping back to step 3.

5. Synthesis & citation – After gathering all needed data, the LLM writes a cohesive, multi‑section
   response, inserting citations that point back to the DuckDuckGo URLs (or titles) it used.

6. Post‑processing – The tool formats the output (Markdown, code blocks, tables) and optionally runs a short
   validation step (e.g., checking that URLs are reachable) before presenting the final research report to
   you.
