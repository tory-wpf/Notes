package notes.controller;

import notes.entity.NoteEntity;
import notes.entity.SearchEntity;
import notes.entity.UserEntity;
import notes.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/notes")
public class NoteController {
    private long GlobalId = 0;
    private final NoteRepository library;
    UserEntity userEntity = new UserEntity();

    @Autowired
    public NoteController(NoteRepository library) {
        this.library = library;
    }

    @GetMapping("")
    public String index (@ModelAttribute("SearchEntity") SearchEntity searchEntity, Model model, Principal principal){
        Iterable<NoteEntity> notes = library.findAllByUser(principal.getName());
        userEntity.setUsername(principal.getName());
        model.addAttribute("notes", notes);
        model.addAttribute("SearchEntity", new SearchEntity());
        model.addAttribute("UserEntity", userEntity);
        return "notes/index";
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("SearchEntity") SearchEntity searchEntity, Model model, Principal principal){

        model.addAttribute("NoteEntity", new NoteEntity());
        model.addAttribute("SearchEntity", new SearchEntity());
        model.addAttribute("UserEntity", userEntity);
        Iterable<NoteEntity> notes = library.findAllByUser(principal.getName());
        model.addAttribute("notes", notes);
        return "notes/new";
    }

    @PostMapping("")
    public String create(@ModelAttribute("NoteEntity") NoteEntity noteEntity, Principal principal) {
        NoteEntity s = new NoteEntity(noteEntity.getName(), noteEntity.getText());
        s.setUser(principal.getName());
        library.save(s);
        Iterable<NoteEntity> notes = library.findAllByUser(principal.getName());
        return "redirect:/notes";
    }

    @GetMapping("/{id}")
    public String show(@ModelAttribute("SearchEntity") SearchEntity searchEntity, @PathVariable("id") long id, Model model, Principal principal){
        Optional<NoteEntity> noteEntity = library.findById(id);
        model.addAttribute("NoteEntity",noteEntity.get());
        Iterable<NoteEntity> notes = library.findAllByUser(principal.getName());
        model.addAttribute("notes", notes);
        model.addAttribute("SearchEntity", new SearchEntity());
        model.addAttribute("UserEntity", userEntity);
        return "notes/show";
    }


    @PostMapping("/search")
    public String sendSearch(@ModelAttribute("SearchEntity") SearchEntity searchEntity, Model model){
        model.addAttribute("SearchEntity", searchEntity);
        if (searchEntity.getTextNote().equals("") | searchEntity.getTextNote()==null){
            return "/errorSearch";
        }
        return "redirect:/notes/search/" + searchEntity.getTextNote();
    }

    @GetMapping("/search/{searchName}")
    public String showSearch(@ModelAttribute("SearchEntity") SearchEntity searchEntity, @PathVariable("searchName") String searchName, Model model, Principal principal){
        Iterable<NoteEntity> notes = library.findAllByUser(principal.getName());
        model.addAttribute("notes", notes);
        model.addAttribute("UserEntity", userEntity);
        List<NoteEntity> searchNoteEntity = library.findByName(searchName);
        List<NoteEntity> showSearchNoteEntity = new ArrayList<NoteEntity>();
        for(NoteEntity note : searchNoteEntity ){
            if (principal.getName().equals(note.getUser())){
                showSearchNoteEntity.add(note);    
            }
        }
        model.addAttribute("SearchEntity", new SearchEntity());
        if (showSearchNoteEntity.isEmpty()) {
            return "notes/errorSearch";
        }
        model.addAttribute("searchNoteEntity",showSearchNoteEntity);
        return "notes/search";
    }

    @GetMapping("/{id}/edit")
    public String edit(@ModelAttribute("SearchEntity") SearchEntity searchEntity, Model model, @PathVariable("id") long id, Principal principal){
        Optional<NoteEntity> noteEntity = library.findById(id);
        model.addAttribute("NoteEntity",noteEntity.get());
        Iterable<NoteEntity> notes = library.findAllByUser(principal.getName());
        model.addAttribute("notes", notes);
        model.addAttribute("SearchEntity", new SearchEntity());
        GlobalId = id;
        model.addAttribute("UserEntity", userEntity);
        return "notes/edit";
    }

    @PostMapping("/edit")
    public String update(@ModelAttribute("NoteEntity") NoteEntity noteEntity, Principal principal) {
        Optional<NoteEntity> s = library.findById(GlobalId);
        s.get().setId(GlobalId);
        s.get().setText(noteEntity.getText());
        s.get().setName(noteEntity.getName());
        s.get().setUser(principal.getName());
        library.save(s.get());
        return "redirect:/notes";
    }

    @PostMapping("/{id}")
    public String delete(@PathVariable("id") long id){
        library.deleteById(id);
        return "redirect:/notes";
    }

}
